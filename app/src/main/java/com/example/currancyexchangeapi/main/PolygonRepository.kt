package com.example.currancyexchangeapi.main

import com.example.currancyexchangeapi.api.PolygonApiService


class PolygonRepository constructor(private val polygonApiService: PolygonApiService) {

    suspend fun getTickers(market: String, active: Boolean, limit: Int, apiKey: String) =
        polygonApiService.getTickers(market, active, limit, apiKey)

    suspend fun getDailyForexData(
        ticker: String,
        from: String,
        to: String,
        adjusted: Boolean,
        sort: String,
        limit: Int,
        apiKey: String
    ) = polygonApiService.getDailyForexData(ticker, from, to, adjusted, sort, limit, apiKey)

}