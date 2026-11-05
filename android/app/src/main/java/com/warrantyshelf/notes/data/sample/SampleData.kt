package com.warrantyshelf.notes.data.sample

import com.warrantyshelf.notes.domain.model.RepairEntry
import com.warrantyshelf.notes.domain.model.ShelfItem

object SampleData {

    val ROOMS = listOf("KITCHEN", "LIVING ROOM", "UTILITY ROOM", "BEDROOM", "HALLWAY")

    val WARRANTY_CHOICES = listOf(12, 24, 36, 60)

    fun seed(todayEpochDay: Long): List<ShelfItem> = listOf(
        ShelfItem(
            id = 1,
            name = "Fridge Freezer",
            room = "KITCHEN",
            serialNote = "SN-4471-K",
            acquiredEpochDay = todayEpochDay - 308,
            warrantyMonths = 24,
            retired = false,
            repairs = listOf(
                RepairEntry(todayEpochDay - 96, "Door seal replaced under cover", 0)
            )
        ),
        ShelfItem(
            id = 2,
            name = "Washing Machine",
            room = "UTILITY ROOM",
            serialNote = "SN-8812-W",
            acquiredEpochDay = todayEpochDay - 480,
            warrantyMonths = 36,
            retired = false,
            repairs = listOf(
                RepairEntry(todayEpochDay - 240, "Drain pump cleaned", 42),
                RepairEntry(todayEpochDay - 54, "Drum bearing noise checked", 0)
            )
        ),
        ShelfItem(
            id = 3,
            name = "Dishwasher",
            room = "KITCHEN",
            serialNote = "SN-2309-D",
            acquiredEpochDay = todayEpochDay - 420,
            warrantyMonths = 24,
            retired = false,
            repairs = emptyList()
        ),
        ShelfItem(
            id = 4,
            name = "Smart TV",
            room = "LIVING ROOM",
            serialNote = "SN-5560-T",
            acquiredEpochDay = todayEpochDay - 400,
            warrantyMonths = 12,
            retired = false,
            repairs = listOf(
                RepairEntry(todayEpochDay - 130, "Backlight strip swapped", 138)
            )
        ),
        ShelfItem(
            id = 5,
            name = "Microwave Oven",
            room = "KITCHEN",
            serialNote = "SN-7731-M",
            acquiredEpochDay = todayEpochDay - 676,
            warrantyMonths = 24,
            retired = false,
            repairs = emptyList()
        ),
        ShelfItem(
            id = 6,
            name = "Vacuum Cleaner",
            room = "HALLWAY",
            serialNote = "SN-1194-V",
            acquiredEpochDay = todayEpochDay - 540,
            warrantyMonths = 24,
            retired = false,
            repairs = listOf(
                RepairEntry(todayEpochDay - 80, "Filter cassette renewed", 19)
            )
        ),
        ShelfItem(
            id = 7,
            name = "Air Purifier",
            room = "BEDROOM",
            serialNote = "SN-6027-A",
            acquiredEpochDay = todayEpochDay - 260,
            warrantyMonths = 36,
            retired = false,
            repairs = emptyList()
        ),
        ShelfItem(
            id = 8,
            name = "Coffee Machine",
            room = "KITCHEN",
            serialNote = "SN-3348-C",
            acquiredEpochDay = todayEpochDay - 299,
            warrantyMonths = 12,
            retired = false,
            repairs = listOf(
                RepairEntry(todayEpochDay - 35, "Descaling valve replaced", 27)
            )
        )
    )
}
