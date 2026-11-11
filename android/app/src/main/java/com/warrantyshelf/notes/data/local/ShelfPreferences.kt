package com.warrantyshelf.notes.data.local

import android.content.Context
import com.warrantyshelf.notes.domain.model.ShelfSettings

class ShelfPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readShelf(): String? = prefs.getString(KEY_SHELF, null)

    fun writeShelf(raw: String) {
        prefs.edit().putString(KEY_SHELF, raw).apply()
    }

    fun readSettings(): ShelfSettings = ShelfSettings(
        accent = prefs.getString(KEY_ACCENT, null) ?: ShelfSettings.DEFAULT.accent,
        currency = prefs.getString(KEY_CURRENCY, null) ?: ShelfSettings.DEFAULT.currency,
        isoDates = prefs.getBoolean(KEY_ISO, ShelfSettings.DEFAULT.isoDates)
    )

    fun writeSettings(settings: ShelfSettings) {
        prefs.edit()
            .putString(KEY_ACCENT, settings.accent)
            .putString(KEY_CURRENCY, settings.currency)
            .putBoolean(KEY_ISO, settings.isoDates)
            .apply()
    }

    private companion object {
        const val STORE = "warranty_shelf_store"
        const val KEY_SHELF = "shelf_items"
        const val KEY_ACCENT = "accent"
        const val KEY_CURRENCY = "currency"
        const val KEY_ISO = "iso_dates"
    }
}
