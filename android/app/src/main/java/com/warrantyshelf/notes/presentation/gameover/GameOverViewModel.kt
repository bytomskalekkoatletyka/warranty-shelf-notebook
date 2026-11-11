package com.warrantyshelf.notes.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warrantyshelf.notes.core.text.ShelfFormat
import com.warrantyshelf.notes.domain.repository.ShelfRepository
import com.warrantyshelf.notes.domain.usecase.CalculateCoverageUseCase
import com.warrantyshelf.notes.domain.usecase.SummariseReviewUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameOverUiState(
    val loading: Boolean = true,
    val itemsOnShelf: Int = 0,
    val totalRepairs: Int = 0
)

class GameOverViewModel(
    private val shelfRepository: ShelfRepository
) : ViewModel() {

    private val coverage = CalculateCoverageUseCase()
    private val summarise = SummariseReviewUseCase(coverage)

    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState: StateFlow<GameOverUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { shelfRepository.items() }
            val summary = summarise(loaded, ShelfFormat.today())
            _uiState.value = GameOverUiState(
                loading = false,
                itemsOnShelf = summary.active,
                totalRepairs = summary.repairs
            )
        }
    }
}
