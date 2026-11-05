package com.warrantyshelf.notes.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warrantyshelf.notes.core.text.ShelfFormat
import com.warrantyshelf.notes.domain.model.ShelfItem
import com.warrantyshelf.notes.domain.repository.ShelfRepository
import com.warrantyshelf.notes.domain.usecase.CalculateCoverageUseCase
import com.warrantyshelf.notes.domain.usecase.SortShelfUseCase
import com.warrantyshelf.notes.domain.usecase.SummariseReviewUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuViewModel(
    private val shelfRepository: ShelfRepository
) : ViewModel() {

    private val coverage = CalculateCoverageUseCase()
    private val sortShelf = SortShelfUseCase(coverage)
    private val summarise = SummariseReviewUseCase(coverage)

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var items: List<ShelfItem> = emptyList()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { shelfRepository.items() }
            items = loaded
            publish()
        }
    }

    fun toggleSearch() {
        _uiState.update { it.copy(searchOpen = !it.searchOpen, query = "") }
        publish()
    }

    fun updateQuery(value: String) {
        _uiState.update { it.copy(query = value) }
        publish()
    }

    fun openAdd() {
        _uiState.update { it.copy(addOpen = true) }
    }

    fun dismissAdd() {
        _uiState.update { it.copy(addOpen = false) }
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
            publish()
        }
    }

    private fun publish() {
        val today = ShelfFormat.today()
        val summary = summarise(items, today)
        val ordered = sortShelf(items, today)
        val query = _uiState.value.query.trim().lowercase()
        val visible = if (query.isEmpty()) {
            ordered
        } else {
            ordered.filter {
                it.name.lowercase().contains(query) || it.room.lowercase().contains(query)
            }
        }
        _uiState.update { state ->
            state.copy(
                loading = false,
                activeItems = summary.active,
                coveredCount = summary.covered,
                endingSoonCount = summary.endingSoon,
                repairsCount = summary.repairs,
                firstItemId = ordered.firstOrNull()?.id ?: -1,
                rows = visible.map { item ->
                    val days = coverage.daysRemaining(item, today)
                    ShelfRow(
                        id = item.id,
                        name = item.name,
                        room = item.room,
                        daysRemaining = days,
                        band = coverage.band(days)
                    )
                }
            )
        }
    }
}
