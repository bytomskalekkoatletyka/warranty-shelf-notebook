package com.warrantyshelf.notes.domain.model

data class ShelfSummary(
    val active: Int,
    val covered: Int,
    val endingSoon: Int,
    val expired: Int,
    val repairs: Int
)
