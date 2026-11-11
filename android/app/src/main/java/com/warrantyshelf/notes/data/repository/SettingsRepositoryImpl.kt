package com.warrantyshelf.notes.data.repository

import com.warrantyshelf.notes.data.local.ShelfPreferences
import com.warrantyshelf.notes.domain.model.ShelfSettings
import com.warrantyshelf.notes.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: ShelfPreferences) : SettingsRepository {

    override fun settings(): ShelfSettings = preferences.readSettings()

    override fun save(settings: ShelfSettings) {
        preferences.writeSettings(settings)
    }
}
