package com.warrantyshelf.notes.domain.model

data class ShelfItem(
    val id: Int,
    val name: String,
    val room: String,
    val serialNote: String,
    val acquiredEpochDay: Long,
    val warrantyMonths: Int,
    val retired: Boolean,
    val repairs: List<RepairEntry>
)
