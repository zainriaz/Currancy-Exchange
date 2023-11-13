package com.example.currancyexchangeapi.data

data class Result(
    val active: Boolean,
    val base_currency_name: String,
    val base_currency_symbol: String,
    val currency_name: String,
    val currency_symbol: String,
    val last_updated_utc: String,
    val locale: String,
    val market: String,
    val name: String,
    val ticker: String
)