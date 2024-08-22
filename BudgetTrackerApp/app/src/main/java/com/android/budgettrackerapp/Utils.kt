package com.android.budgettrackerapp

enum class Currency(private val symbol: String) {
    RON("RON"),
    EUR("EUR"),
    USD("USD");

    override fun toString(): String {
        return symbol
    }
}

object CurrencySettings {
    var currency: Currency = Currency.RON
}