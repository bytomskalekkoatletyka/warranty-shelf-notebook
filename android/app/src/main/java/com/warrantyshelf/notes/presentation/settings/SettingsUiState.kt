package com.warrantyshelf.notes.presentation.settings

import com.warrantyshelf.notes.domain.model.ShelfSettings

data class SettingsUiState(
    val loading: Boolean = true,
    val settings: ShelfSettings = ShelfSettings.DEFAULT
)
