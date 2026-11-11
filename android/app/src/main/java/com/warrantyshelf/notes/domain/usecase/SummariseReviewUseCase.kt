package com.warrantyshelf.notes.domain.usecase

import com.warrantyshelf.notes.domain.model.CoverageBand
import com.warrantyshelf.notes.domain.model.ShelfItem
import com.warrantyshelf.notes.domain.model.ShelfSummary

class SummariseReviewUseCase(private val coverage: CalculateCoverageUseCase) {

    operator fun invoke(items: List<ShelfItem>, todayEpochDay: Long): ShelfSummary {
        val active = items.filterNot { it.retired }
        val bands = active.map { coverage.bandOf(it, todayEpochDay) }
        return ShelfSummary(
            active = active.size,
            covered = bands.count { it == CoverageBand.COVERED },
            endingSoon = bands.count { it == CoverageBand.ENDING_SOON },
            expired = bands.count { it == CoverageBand.EXPIRED },
            repairs = active.sumOf { it.repairs.size }
        )
    }
}
