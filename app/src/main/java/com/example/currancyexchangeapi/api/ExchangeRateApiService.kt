package com.example.currancyexchangeapi.api

import com.example.currancyexchangeapi.data.ConversionRates
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigDecimal

interface ExchangeRateApiService {

    @GET("latest/EUR")
    suspend fun getLatestExchangeRates() : Response<ConversionRates>

    companion object {
        private const val API_KEY = "21d1fd10f271efa78225a715"
        private var exchangeRateApiService: ExchangeRateApiService? = null
        fun getInstance() : ExchangeRateApiService {
            if (exchangeRateApiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://v6.exchangerate-api.com/v6/$API_KEY/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                exchangeRateApiService = retrofit.create(ExchangeRateApiService::class.java)
            }
            return exchangeRateApiService!!
        }

    }
}