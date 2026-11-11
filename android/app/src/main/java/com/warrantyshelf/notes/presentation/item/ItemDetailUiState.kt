package com.warrantyshelf.notes.presentation.item

import com.warrantyshelf.notes.domain.model.CoverageBand

data class RepairRow(
    val dateText: String,
    val note: String,
    val costText: String
)

data class ItemDetailUiState(
    val loading: Boolean = true,
    val found: Boolean = false,
    val itemId: Int = -1,
    val name: String = "",
    val room: String = "",
    val serialText: String = "",
    val acquiredText: String = "",
    val coverEndsText: String = "",
    val warrantyText: String = "",
    val daysRemaining: Int = 0,
    val band: CoverageBand = CoverageBand.COVERED,
    val retired: Boolean = false,
    val repairs: List<RepairRow> = emptyList(),
    val documentsExpanded: Boolean = false,
    val repairOpen: Boolean = false,
    val retireOpen: Boolean = false
)
