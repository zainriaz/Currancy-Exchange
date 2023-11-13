package com.example.currancyexchangeapi.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.currancyexchangeapi.adapter.RatesAdapter
import com.example.currancyexchangeapi.api.ExchangeRateApiService
import com.example.currancyexchangeapi.data.ConversionRates
import com.example.currancyexchangeapi.databinding.FragmentRatesBinding
import com.example.currancyexchangeapi.main.ExchangeRateRepository
import com.example.currancyexchangeapi.main.ExchangeRateViewModel
import com.example.currancyexchangeapi.main.ExchangeRateViewModelFactory

class RatesFragment : Fragment() {

    private lateinit var viewModel: ExchangeRateViewModel
    private lateinit var binding: FragmentRatesBinding
    private var amount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRatesBinding.inflate(inflater, container, false)

        val exchangeRateApiService = ExchangeRateApiService.getInstance()
        val exchangeRateRepository = ExchangeRateRepository(exchangeRateApiService)
        viewModel = ViewModelProvider(this, ExchangeRateViewModelFactory(exchangeRateRepository))[ExchangeRateViewModel::class.java]

        // Assuming you have set android:imeOptions="actionDone" in your XML
        // Assuming you have set android:imeOptions="actionDone" in your XML
        binding.inputAmount.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Perform the action you want when the "Done" button is clicked
                // For example, hide the keyboard
                if (binding.inputAmount.text.isNotBlank()) {
                    amount = binding.inputAmount.text.toString().toInt()
                    getEURExchangeRates()
                    hideKeyboard(binding.inputAmount)
                }else{
                    binding.inputAmount.error = "Amount required"
                }
                return@setOnEditorActionListener true
            }
            false
        }

        getEURExchangeRates()

        return binding.root
    }

    private fun getEURExchangeRates() {
        viewModel.exchangeRate.observe(viewLifecycleOwner) {
            Log.d(TAG, "Response: $it")
            if (isAdded) {
                it?.let {
                    populateRatesList(it)
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (isAdded) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadingConversion.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.getLatestExchangeRates()
    }

    private fun populateRatesList(rates: ConversionRates) {
        binding.rvExchangeRates.hasFixedSize()
        binding.rvExchangeRates.layoutManager = LinearLayoutManager(requireContext())
        val currenciesList = rates.conversion_rates.keys.toList()
        val ratesList = rates.conversion_rates.values.toList()
        val adapter = RatesAdapter(currenciesList, ratesList, amount)
        binding.rvExchangeRates.adapter = adapter
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    companion object {

        private val TAG: String? = "RatesFragment"
    }
}