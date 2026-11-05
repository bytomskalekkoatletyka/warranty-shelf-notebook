package com.warrantyshelf.notes.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warrantyshelf.notes.domain.repository.SettingsRepository
import com.warrantyshelf.notes.domain.repository.ShelfRepository
import com.warrantyshelf.notes.presentation.app.AppViewModel
import com.warrantyshelf.notes.presentation.game.GameViewModel
import com.warrantyshelf.notes.presentation.gameover.GameOverViewModel
import com.warrantyshelf.notes.presentation.item.ItemDetailViewModel
import com.warrantyshelf.notes.presentation.menu.MenuViewModel
import com.warrantyshelf.notes.presentation.settings.SettingsViewModel
import com.warrantyshelf.notes.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val shelf: ShelfRepository,
    private val settings: SettingsRepository,
    private val itemId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(shelf)
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(shelf, settings)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(shelf)
            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) ->
                ItemDetailViewModel(shelf, settings, itemId)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(settings)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
