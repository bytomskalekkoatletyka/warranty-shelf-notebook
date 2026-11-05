package com.warrantyshelf.notes.presentation.app

import androidx.lifecycle.ViewModel
import com.warrantyshelf.notes.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private var sessionCounter = 0

    fun nextSession(): Int {
        sessionCounter += 1
        return sessionCounter
    }

    fun navigate(target: Screen) {
        backStack.addLast(_screen.value)
        _screen.value = target
    }

    fun navigateReplacing(target: Screen) {
        _screen.value = target
    }

    fun navigateRoot(target: Screen) {
        backStack.clear()
        _screen.value = target
    }

    fun back() {
        _screen.value = backStack.removeLastOrNull() ?: Screen.Menu
    }
}
