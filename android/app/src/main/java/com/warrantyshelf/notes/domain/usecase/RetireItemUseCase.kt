package com.warrantyshelf.notes.domain.usecase

import com.warrantyshelf.notes.domain.model.ShelfItem

class RetireItemUseCase {

    operator fun invoke(items: List<ShelfItem>, itemId: Int): List<ShelfItem> =
        items.map { item ->
            if (item.id == itemId) item.copy(retired = true) else item
        }
}
