package com.example.currancyexchangeapi.main

import com.example.currancyexchangeapi.api.ExchangeRateApiService

class ExchangeRateRepository constructor(private val exchangeRateApiService: ExchangeRateApiService) {

    suspend fun getLatestExchangeRates() = exchangeRateApiService.getLatestExchangeRates()

}