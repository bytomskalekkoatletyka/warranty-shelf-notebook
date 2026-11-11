package com.warrantyshelf.notes.presentation.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warrantyshelf.notes.core.text.ShelfFormat
import com.warrantyshelf.notes.domain.model.RepairEntry
import com.warrantyshelf.notes.domain.model.ShelfItem
import com.warrantyshelf.notes.domain.model.ShelfSettings
import com.warrantyshelf.notes.domain.repository.SettingsRepository
import com.warrantyshelf.notes.domain.repository.ShelfRepository
import com.warrantyshelf.notes.domain.usecase.CalculateCoverageUseCase
import com.warrantyshelf.notes.domain.usecase.LogRepairUseCase
import com.warrantyshelf.notes.domain.usecase.RetireItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailViewModel(
    private val shelfRepository: ShelfRepository,
    private val settingsRepository: SettingsRepository,
    private val itemId: Int
) : ViewModel() {

    private val coverage = CalculateCoverageUseCase()
    private val logRepair = LogRepairUseCase()
    private val retireItem = RetireItemUseCase()

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    private var items: List<ShelfItem> = emptyList()
    private var settings: ShelfSettings = ShelfSettings.DEFAULT

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            items = withContext(Dispatchers.IO) { shelfRepository.items() }
            settings = withContext(Dispatchers.IO) { settingsRepository.settings() }
            render()
        }
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

    fun saveRepair(note: String, cost: Int) {
        val entry = RepairEntry(ShelfFormat.today(), note.trim(), cost)
        val updated = logRepair(items, itemId, entry)
        items = updated
        _uiState.update { it.copy(repairOpen = false) }
        persist(updated)
    }

    fun confirmRetire() {
        val updated = retireItem(items, itemId)
        items = updated
        _uiState.update { it.copy(retireOpen = false) }
        persist(updated)
    }

    private fun persist(updated: List<ShelfItem>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { shelfRepository.save(updated) }
            render()
        }
    }

    private fun render() {
        val item = items.firstOrNull { it.id == itemId }
        if (item == null) {
            _uiState.update { it.copy(loading = false, found = false) }
            return
        }
        val days = coverage.daysRemaining(item, ShelfFormat.today())
        _uiState.update {
            it.copy(
                loading = false,
                found = true,
                itemId = item.id,
                name = item.name,
                room = item.room,
                serialText = item.serialNote.ifBlank { "—" },
                acquiredText = ShelfFormat.date(item.acquiredEpochDay, settings.isoDates),
                coverEndsText = ShelfFormat.date(
                    coverage.coverEndsEpochDay(item),
                    settings.isoDates
                ),
                warrantyText = ShelfFormat.count(item.warrantyMonths),
                daysRemaining = days,
                band = coverage.band(days),
                retired = item.retired,
                repairs = item.repairs.map { entry ->
                    RepairRow(
                        dateText = ShelfFormat.date(entry.epochDay, settings.isoDates),
                        note = entry.note,
                        costText = if (entry.cost > 0) {
                            settings.currency + " " + ShelfFormat.count(entry.cost)
                        } else {
                            ""
                        }
                    )
                }
            )
        }
    }
}
