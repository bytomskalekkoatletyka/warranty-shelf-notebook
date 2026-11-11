package com.warrantyshelf.notes.domain.repository

import com.warrantyshelf.notes.domain.model.ShelfSettings

interface SettingsRepository {
    fun settings(): ShelfSettings
    fun save(settings: ShelfSettings)
}
