package com.warrantyshelf.notes.domain.usecase

import com.warrantyshelf.notes.core.config.GameConfig
import com.warrantyshelf.notes.domain.model.CoverageBand
import com.warrantyshelf.notes.domain.model.ShelfItem

class CalculateCoverageUseCase {

    fun daysRemaining(item: ShelfItem, todayEpochDay: Long): Int {
        val cover = item.acquiredEpochDay + item.warrantyMonths.toLong() * GameConfig.DAYS_PER_MONTH
        return (cover - todayEpochDay).toInt()
    }

    fun coverEndsEpochDay(item: ShelfItem): Long =
        item.acquiredEpochDay + item.warrantyMonths.toLong() * GameConfig.DAYS_PER_MONTH

    fun band(daysRemaining: Int): CoverageBand = when {
        daysRemaining <= 0 -> CoverageBand.EXPIRED
        daysRemaining <= GameConfig.WARRANTY_SOON_DAYS -> CoverageBand.ENDING_SOON
        else -> CoverageBand.COVERED
    }

    fun bandOf(item: ShelfItem, todayEpochDay: Long): CoverageBand =
        band(daysRemaining(item, todayEpochDay))
}
