package com.warrantyshelf.notes.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warrantyshelf.notes.core.config.GameConfig
import com.warrantyshelf.notes.core.text.ShelfFormat
import com.warrantyshelf.notes.domain.model.CoverageBand
import com.warrantyshelf.notes.domain.model.RepairEntry
import com.warrantyshelf.notes.domain.model.ShelfItem
import com.warrantyshelf.notes.domain.repository.SettingsRepository
import com.warrantyshelf.notes.domain.repository.ShelfRepository
import com.warrantyshelf.notes.domain.usecase.CalculateCoverageUseCase
import com.warrantyshelf.notes.domain.usecase.LogRepairUseCase
import com.warrantyshelf.notes.domain.usecase.RetireItemUseCase
import com.warrantyshelf.notes.domain.usecase.SortShelfUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface GameEvent {
    data class Finished(
        val kept: Int,
        val repairs: Int,
        val retired: Int,
        val endingSoon: Int
    ) : GameEvent
}

class GameViewModel(
    private val shelfRepository: ShelfRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val coverage = CalculateCoverageUseCase()
    private val sortShelf = SortShelfUseCase(coverage)
    private val logRepair = LogRepairUseCase()
    private val retireItem = RetireItemUseCase()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var items: List<ShelfItem> = emptyList()
    private var queue: List<Int> = emptyList()
    private var index = 0
    private var kept = 0
    private var repairsLogged = 0
    private var retiredCount = 0
    private var activeSession = Int.MIN_VALUE
    private var stepJob: Job? = null
    private var isoDates = false

    fun startSession(session: Int) {
        if (activeSession == session) return
        activeSession = session
        stepJob?.cancel()
        kept = 0
        repairsLogged = 0
        retiredCount = 0
        index = 0
        _uiState.value = GameUiState()
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { shelfRepository.items() }
            isoDates = withContext(Dispatchers.IO) { settingsRepository.settings().isoDates }
            items = loaded
            rebuildQueue()
        }
    }

    fun pause() {
        stepJob?.cancel()
    }

    fun openPause() {
        _uiState.update { it.copy(pauseOpen = true) }
    }

    fun dismissPause() {
        _uiState.update { it.copy(pauseOpen = false) }
    }

    fun toggleDocuments() {
        _uiState.update { it.copy(documentsExpanded = !it.documentsExpanded) }
    }

    fun openRepair() {
        _uiState.update { it.copy(repairOpen = true) }
    }

    fun dismissRepair() {
        _uiState.update { it.copy(repairOpen = false) }
    }

    fun openRetire() {
        _uiState.update { it.copy(retireOpen = true) }
    }

    fun dismissRetire() {
        _uiState.update { it.copy(retireOpen = false) }
    }

    fun openAdd() {
        _uiState.update { it.copy(addOpen = true) }
    }

    fun dismissAdd() {
        _uiState.update { it.copy(addOpen = false) }
    }

    fun keep() {
        if (currentId() < 0) return
        kept += 1
        advance(null)
    }

    fun saveRepair(note: String, cost: Int) {
        val id = currentId()
        if (id < 0) return
        val entry = RepairEntry(ShelfFormat.today(), note.trim(), cost)
        val updated = logRepair(items, id, entry)
        items = updated
        repairsLogged += 1
        advance(updated)
    }

    fun confirmRetire() {
        val id = currentId()
        if (id < 0) return
        val updated = retireItem(items, id)
        items = updated
        retiredCount += 1
        advance(updated)
    }

    fun addItem(name: String, room: String, warrantyMonths: Int) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
        val created = ShelfItem(
            id = nextId,
            name = trimmed,
            room = room,
            serialNote = "",
            acquiredEpochDay = ShelfFormat.today(),
            warrantyMonths = warrantyMonths,
            retired = false,
            repairs = emptyList()
        )
        val updated = items + created
        items = updated
        _uiState.update { it.copy(addOpen = false) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) { shelfRepository.save(updated) }
            index = 0
            rebuildQueue()
        }
    }

    fun finishNow() {
        stepJob?.cancel()
        stepJob = viewModelScope.launch { emitSummary() }
    }

    private fun currentId(): Int = queue.getOrNull(index) ?: -1

    private fun rebuildQueue() {
        val today = ShelfFormat.today()
        queue = sortShelf(items, today)
            .take(GameConfig.MAX_REVIEW_ITEMS)
            .map { it.id }
        if (queue.isEmpty()) {
            _uiState.update {
                it.copy(
                    loading = false,
                    empty = true,
                    step = 0,
                    total = 0,
                    progress = 0f,
                    itemId = -1
                )
            }
        } else {
            render()
        }
    }

    private fun advance(toPersist: List<ShelfItem>?) {
        stepJob?.cancel()
        stepJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sealVisible = true,
                    saving = toPersist != null,
                    repairOpen = false,
                    retireOpen = false
                )
            }
            if (toPersist != null) {
                withContext(Dispatchers.IO) { shelfRepository.save(toPersist) }
            }
            delay(GameConfig.SEAL_HOLD_MS)
            _uiState.update { it.copy(sealVisible = false, saving = false) }
            index += 1
            if (index >= queue.size) {
                emitSummary()
            } else {
                render()
            }
        }
    }

    private suspend fun emitSummary() {
        val today = ShelfFormat.today()
        val endingSoon = items.filterNot { it.retired }
            .count { coverage.bandOf(it, today) == CoverageBand.ENDING_SOON }
        eventChannel.send(
            GameEvent.Finished(
                kept = kept,
                repairs = repairsLogged,
                retired = retiredCount,
                endingSoon = endingSoon
            )
        )
    }

    private fun render() {
        val id = queue.getOrNull(index) ?: return
        val item = items.firstOrNull { it.id == id } ?: return
        val today = ShelfFormat.today()
        val days = coverage.daysRemaining(item, today)
        _uiState.update {
            it.copy(
                loading = false,
                empty = false,
                step = index + 1,
                total = queue.size,
                progress = (index + 1).toFloat() / queue.size.toFloat(),
                itemId = item.id,
                itemName = item.name,
                room = item.room,
                acquiredText = ShelfFormat.date(item.acquiredEpochDay, isoDates),
                coverEndsText = ShelfFormat.date(coverage.coverEndsEpochDay(item), isoDates),
                serialText = item.serialNote.ifBlank { "—" },
                repairsText = ShelfFormat.count(item.repairs.size),
                daysRemaining = days,
                band = coverage.band(days),
                kept = kept,
                repairsLogged = repairsLogged,
                retired = retiredCount,
                documentsExpanded = false
            )
        }
    }
}
