package com.android.budgettrackerapp

enum class Currency(private val symbol: String) {
    RON("RON"),
    EUR("EUR"),
    USD("USD"),
    GBP("GBP");

    override fun toString(): String {
        if (this == USD) {
            return "$"
        } else if (this == EUR) {
            return "€"
        } else if (this == GBP) {
            return "£"
        }
        return symbol
    }
}

object CurrencySettings {
    var currency: Currency = Currency.RON
}