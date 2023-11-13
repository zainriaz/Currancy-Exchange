package com.example.currancyexchangeapi.api

import android.content.Context
import com.example.currancyexchangeapi.data.Ticker
import com.example.currancyexchangeapi.data.forex.ForexDataResponse
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface PolygonApiService {



    @GET("v3/reference/tickers")
    @Headers("Cache-Control: max-age=600") // Cache response for 10 minutes
    suspend fun getTickers(@Query("market") market: String,
                           @Query("active") active: Boolean,
                           @Query("limit") limit: Int,
                           @Query("apiKey") apiKey: String): Response<Ticker>

    @GET("v2/aggs/ticker/{ticker}/range/1/day/{from}/{to}")
    suspend fun getDailyForexData(
        @Path("ticker") ticker: String,
        @Path("from") from: String,
        @Path("to") to: String,
        @Query("adjusted") adjusted: Boolean = true,
        @Query("sort") sort: String = "asc",
        @Query("limit") limit: Int = 1,
        @Query("apiKey") apiKey: String
    ): Response<ForexDataResponse>

    companion object {

        private var polygonApiService: PolygonApiService? = null
        private const val BASE_URL = "https://api.polygon.io/"

        fun getInstance(context: Context): PolygonApiService {
            val cacheSize = (5 * 1024 * 1024).toLong() // 5 MB
            val cache = Cache(context.cacheDir, cacheSize)

            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .build()
            if (polygonApiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                polygonApiService = retrofit.create(PolygonApiService::class.java)
            }
            return polygonApiService!!
        }

    }
}