package com.warrantyshelf.notes.presentation.game

import com.warrantyshelf.notes.domain.model.CoverageBand

data class GameUiState(
    val loading: Boolean = true,
    val empty: Boolean = false,
    val step: Int = 0,
    val total: Int = 0,
    val progress: Float = 0f,
    val itemId: Int = -1,
    val itemName: String = "",
    val room: String = "",
    val acquiredText: String = "",
    val coverEndsText: String = "",
    val serialText: String = "",
    val repairsText: String = "",
    val daysRemaining: Int = 0,
    val band: CoverageBand = CoverageBand.COVERED,
    val kept: Int = 0,
    val repairsLogged: Int = 0,
    val retired: Int = 0,
    val saving: Boolean = false,
    val sealVisible: Boolean = false,
    val documentsExpanded: Boolean = false,
    val pauseOpen: Boolean = false,
    val repairOpen: Boolean = false,
    val retireOpen: Boolean = false,
    val addOpen: Boolean = false
)
