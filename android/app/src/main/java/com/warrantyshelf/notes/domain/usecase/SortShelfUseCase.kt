package com.warrantyshelf.notes.domain.usecase

import com.warrantyshelf.notes.domain.model.ShelfItem

class SortShelfUseCase(private val coverage: CalculateCoverageUseCase) {

    operator fun invoke(items: List<ShelfItem>, todayEpochDay: Long): List<ShelfItem> =
        items.filterNot { it.retired }
            .sortedBy { coverage.daysRemaining(it, todayEpochDay) }
}
