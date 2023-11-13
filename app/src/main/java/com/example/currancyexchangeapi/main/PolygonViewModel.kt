package com.example.currancyexchangeapi.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currancyexchangeapi.data.Ticker
import com.example.currancyexchangeapi.data.forex.ForexDataResponse
import kotlinx.coroutines.*

class PolygonViewModel constructor(private val polygonRepository: PolygonRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val tickers = MutableLiveData<Ticker>()
    val forexDataResponse = MutableLiveData<ForexDataResponse>()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun getTickers(market: String, active: Boolean, limit: Int, apiKey: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = polygonRepository.getTickers(market, active, limit, apiKey)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    tickers.postValue(response.body())
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }

    fun getDailyForexData(ticker: String, from: String, to: String, adjusted: Boolean, sort: String, limit: Int, apiKey: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loading.postValue(true)
            val response = polygonRepository.getDailyForexData(ticker, from, to, adjusted, sort, limit, apiKey)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    forexDataResponse.postValue(response.body())
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}

class PolyGonViewModelFactory constructor(private val repository: PolygonRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PolygonViewModel::class.java)) {
            PolygonViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}