package com.example.currancyexchangeapi.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currancyexchangeapi.data.ConversionRates
import kotlinx.coroutines.*

class ExchangeRateViewModel constructor(private val exchangeRateRepository: ExchangeRateRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val exchangeRate = MutableLiveData<ConversionRates>()
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loadingConversion = MutableLiveData<Boolean>()


    fun getLatestExchangeRates() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            loadingConversion.postValue(true)
            val response = exchangeRateRepository.getLatestExchangeRates()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    exchangeRate.postValue(response.body())
                    loadingConversion.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loadingConversion.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}

class ExchangeRateViewModelFactory constructor(private val repository: ExchangeRateRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ExchangeRateViewModel::class.java)) {
            ExchangeRateViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}