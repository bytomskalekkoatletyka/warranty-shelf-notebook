package com.warrantyshelf.notes.domain.repository

import com.warrantyshelf.notes.domain.model.ShelfItem

interface ShelfRepository {
    fun items(): List<ShelfItem>
    fun save(items: List<ShelfItem>)
}
