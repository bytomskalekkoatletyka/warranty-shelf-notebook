package com.warrantyshelf.notes.core.di

import android.content.Context
import com.warrantyshelf.notes.data.local.ShelfPreferences
import com.warrantyshelf.notes.data.repository.SettingsRepositoryImpl
import com.warrantyshelf.notes.data.repository.ShelfRepositoryImpl
import com.warrantyshelf.notes.domain.repository.SettingsRepository
import com.warrantyshelf.notes.domain.repository.ShelfRepository

object ServiceLocator {

    private var shelfStore: ShelfRepository? = null
    private var settingsStore: SettingsRepository? = null
    private var sharedFactory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (sharedFactory != null) return
        val preferences = ShelfPreferences(context)
        val shelf = ShelfRepositoryImpl(preferences)
        val settings = SettingsRepositoryImpl(preferences)
        shelfStore = shelf
        settingsStore = settings
        sharedFactory = AppViewModelFactory(shelf, settings, NO_ITEM)
    }

    val factory: AppViewModelFactory
        get() = checkNotNull(sharedFactory)

    fun itemFactory(itemId: Int): AppViewModelFactory =
        AppViewModelFactory(checkNotNull(shelfStore), checkNotNull(settingsStore), itemId)

    private const val NO_ITEM = -1
}
