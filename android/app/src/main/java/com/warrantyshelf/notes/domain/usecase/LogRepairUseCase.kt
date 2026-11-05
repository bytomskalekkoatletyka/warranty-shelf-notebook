package com.warrantyshelf.notes.domain.usecase

import com.warrantyshelf.notes.domain.model.RepairEntry
import com.warrantyshelf.notes.domain.model.ShelfItem

class LogRepairUseCase {

    operator fun invoke(items: List<ShelfItem>, itemId: Int, entry: RepairEntry): List<ShelfItem> =
        items.map { item ->
            if (item.id == itemId) item.copy(repairs = item.repairs + entry) else item
        }
}
