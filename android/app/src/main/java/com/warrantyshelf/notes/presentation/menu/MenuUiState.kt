package com.warrantyshelf.notes.presentation.menu

import com.warrantyshelf.notes.domain.model.CoverageBand

data class ShelfRow(
    val id: Int,
    val name: String,
    val room: String,
    val daysRemaining: Int,
    val band: CoverageBand
)

data class MenuUiState(
    val loading: Boolean = true,
    val activeItems: Int = 0,
    val coveredCount: Int = 0,
    val endingSoonCount: Int = 0,
    val repairsCount: Int = 0,
    val firstItemId: Int = -1,
    val searchOpen: Boolean = false,
    val query: String = "",
    val rows: List<ShelfRow> = emptyList(),
    val addOpen: Boolean = false
)
