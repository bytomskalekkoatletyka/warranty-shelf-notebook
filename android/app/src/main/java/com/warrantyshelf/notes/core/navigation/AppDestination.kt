package com.warrantyshelf.notes.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(val session: Int) : Screen

    data class GameOver(
        val kept: Int,
        val repairs: Int,
        val retired: Int,
        val endingSoon: Int
    ) : Screen

    data class ItemDetail(val itemId: Int) : Screen

    data object Settings : Screen
}
