package com.warrantyshelf.notes.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warrantyshelf.notes.core.di.ServiceLocator
import com.warrantyshelf.notes.core.navigation.Screen
import com.warrantyshelf.notes.core.theme.ShelfBackground
import com.warrantyshelf.notes.presentation.game.GameScreen
import com.warrantyshelf.notes.presentation.gameover.GameOverScreen
import com.warrantyshelf.notes.presentation.item.ItemDetailScreen
import com.warrantyshelf.notes.presentation.menu.MenuScreen
import com.warrantyshelf.notes.presentation.settings.SettingsScreen
import com.warrantyshelf.notes.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Splash && screen !is Screen.Menu) {
        appViewModel.back()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = ShelfBackground) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(animationSpec = tween(420)) togetherWith
                    fadeOut(animationSpec = tween(300))
            },
            label = "appScreens"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.navigateRoot(Screen.Menu) }
                )

                is Screen.Menu -> MenuScreen(
                    onStart = { appViewModel.navigate(Screen.Game(appViewModel.nextSession())) },
                    onOpenItem = { id -> appViewModel.navigate(Screen.ItemDetail(id)) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameScreen(
                    session = target.session,
                    onMenu = { appViewModel.navigateRoot(Screen.Menu) },
                    onFinished = { kept, repairs, retired, endingSoon ->
                        appViewModel.navigateReplacing(
                            Screen.GameOver(kept, repairs, retired, endingSoon)
                        )
                    }
                )

                is Screen.GameOver -> GameOverScreen(
                    kept = target.kept,
                    repairs = target.repairs,
                    retired = target.retired,
                    endingSoon = target.endingSoon,
                    onReviewAgain = {
                        appViewModel.navigateReplacing(Screen.Game(appViewModel.nextSession()))
                    },
                    onMenu = { appViewModel.navigateRoot(Screen.Menu) }
                )

                is Screen.ItemDetail -> ItemDetailScreen(
                    itemId = target.itemId,
                    onBack = { appViewModel.back() }
                )

                is Screen.Settings -> SettingsScreen(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
