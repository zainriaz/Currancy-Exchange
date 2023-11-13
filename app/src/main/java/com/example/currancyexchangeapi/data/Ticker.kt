package com.example.currancyexchangeapi.data

data class Ticker(
    val count: Int,
    val next_url: String,
    val request_id: String,
    val results: List<Result>,
    val status: String
)