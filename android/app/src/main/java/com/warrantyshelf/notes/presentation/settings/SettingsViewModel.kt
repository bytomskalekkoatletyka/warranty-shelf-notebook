package com.warrantyshelf.notes.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warrantyshelf.notes.domain.model.ShelfSettings
import com.warrantyshelf.notes.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { settingsRepository.settings() }
            _uiState.value = SettingsUiState(loading = false, settings = loaded)
        }
    }

    fun selectAccent(accent: String) {
        apply(_uiState.value.settings.copy(accent = accent))
    }

    fun selectCurrency(currency: String) {
        apply(_uiState.value.settings.copy(currency = currency))
    }

    fun selectDateFormat(isoDates: Boolean) {
        apply(_uiState.value.settings.copy(isoDates = isoDates))
    }

    private fun apply(updated: ShelfSettings) {
        _uiState.update { it.copy(settings = updated) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) { settingsRepository.save(updated) }
        }
    }
}
