package com.warrantyshelf.notes.domain.model

data class RepairEntry(
    val epochDay: Long,
    val note: String,
    val cost: Int
)
