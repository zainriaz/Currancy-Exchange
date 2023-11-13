package com.example.currancyexchangeapi.data.forex

data class ForexDataResponse(
    val adjusted: Boolean,
    val count: Int,
    val queryCount: Int,
    val request_id: String,
    val results: List<Result>,
    val resultsCount: Int,
    val status: String,
    val ticker: String
)