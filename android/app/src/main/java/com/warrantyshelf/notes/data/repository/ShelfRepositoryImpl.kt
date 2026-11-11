package com.warrantyshelf.notes.data.repository

import com.warrantyshelf.notes.data.local.ShelfPreferences
import com.warrantyshelf.notes.data.sample.SampleData
import com.warrantyshelf.notes.domain.model.RepairEntry
import com.warrantyshelf.notes.domain.model.ShelfItem
import com.warrantyshelf.notes.domain.repository.ShelfRepository
import java.time.LocalDate

class ShelfRepositoryImpl(private val preferences: ShelfPreferences) : ShelfRepository {

    override fun items(): List<ShelfItem> {
        val raw = preferences.readShelf()
        if (raw.isNullOrBlank()) {
            val seeded = SampleData.seed(LocalDate.now().toEpochDay())
            save(seeded)
            return seeded
        }
        return decode(raw)
    }

    override fun save(items: List<ShelfItem>) {
        preferences.writeShelf(encode(items))
    }

    private fun encode(items: List<ShelfItem>): String =
        items.joinToString(ROW) { item ->
            listOf(
                item.id.toString(),
                clean(item.name),
                clean(item.room),
                clean(item.serialNote),
                item.acquiredEpochDay.toString(),
                item.warrantyMonths.toString(),
                if (item.retired) "1" else "0",
                item.repairs.joinToString(REPAIR) { entry -> encodeRepair(entry) }
            ).joinToString(FIELD)
        }

    private fun encodeRepair(entry: RepairEntry): String =
        listOf(
            entry.epochDay.toString(),
            clean(entry.note),
            entry.cost.toString()
        ).joinToString(FIELD)

    private fun decode(raw: String): List<ShelfItem> =
        raw.split(ROW)
            .filter { it.isNotBlank() }
            .mapNotNull { row -> decodeItem(row) }

    private fun decodeItem(row: String): ShelfItem? {
        val parts = row.split(FIELD)
        if (parts.size < 8) return null
        val id = parts[0].toIntOrNull() ?: return null
        val acquired = parts[4].toLongOrNull() ?: return null
        val months = parts[5].toIntOrNull() ?: return null
        return ShelfItem(
            id = id,
            name = parts[1],
            room = parts[2],
            serialNote = parts[3],
            acquiredEpochDay = acquired,
            warrantyMonths = months,
            retired = parts[6] == "1",
            repairs = decodeRepairs(parts[7])
        )
    }

    private fun decodeRepairs(blob: String): List<RepairEntry> {
        if (blob.isBlank()) return emptyList()
        return blob.split(REPAIR).mapNotNull { chunk ->
            val parts = chunk.split(FIELD)
            if (parts.size < 3) {
                null
            } else {
                val day = parts[0].toLongOrNull()
                if (day == null) null else RepairEntry(day, parts[1], parts[2].toIntOrNull() ?: 0)
            }
        }
    }

    private fun clean(value: String): String =
        value.replace(FIELD, " ")
            .replace(ROW, " ")
            .replace(REPAIR, " ")
            .trim()

    private companion object {
        const val FIELD = "\u0001"
        const val ROW = "\u0002"
        const val REPAIR = "\u0003"
    }
}
