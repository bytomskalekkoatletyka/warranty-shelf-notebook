package com.warrantyshelf.notes.core.text

import java.time.LocalDate
import java.util.Locale

object ShelfFormat {

    fun today(): Long = LocalDate.now().toEpochDay()

    fun date(epochDay: Long, isoDates: Boolean): String {
        val day = LocalDate.ofEpochDay(epochDay)
        return if (isoDates) {
            String.format(Locale.US, "%04d-%02d-%02d", day.year, day.monthValue, day.dayOfMonth)
        } else {
            String.format(Locale.US, "%02d.%02d.%04d", day.dayOfMonth, day.monthValue, day.year)
        }
    }

    fun count(value: Int): String = String.format(Locale.US, "%d", value)

    fun dash(value: Int): String = if (value <= 0) "—" else count(value)
}
