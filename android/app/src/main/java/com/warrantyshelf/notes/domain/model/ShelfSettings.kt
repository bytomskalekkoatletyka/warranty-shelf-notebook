package com.warrantyshelf.notes.domain.model

data class ShelfSettings(
    val accent: String,
    val currency: String,
    val isoDates: Boolean
) {
    companion object {
        const val ACCENT_PETROL = "petrol"
        const val ACCENT_ORANGE = "orange"

        val CURRENCIES = listOf("USD", "EUR", "GBP", "PLN")

        val DEFAULT = ShelfSettings(
            accent = ACCENT_PETROL,
            currency = "USD",
            isoDates = false
        )
    }
}
