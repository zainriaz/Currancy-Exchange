package com.example.currancyexchangeapi.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.currancyexchangeapi.R
import com.example.currancyexchangeapi.api.PolygonApiService
import com.example.currancyexchangeapi.data.Ticker
import com.example.currancyexchangeapi.databinding.FragmentChartsBinding
import com.example.currancyexchangeapi.main.PolyGonViewModelFactory
import com.example.currancyexchangeapi.main.PolygonRepository
import com.example.currancyexchangeapi.main.PolygonViewModel
import com.example.currancyexchangeapi.view.MyMarkerView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.zhzc0x.chart.AxisInfo
import com.zhzc0x.chart.PointInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChartsFragment : Fragment() {

    private lateinit var viewModel: PolygonViewModel
    private lateinit var binding: FragmentChartsBinding
    private val apiKey = "w3sm6yIseYG98vpSY2mqvgiGCGirJc2x"
    private var tickers: MutableList<String> = ArrayList()
    private var currencySymbols: MutableList<String> = ArrayList()
    private var mTicker: String = "C:AURUSD"
    private lateinit var currentDateString: String
    private lateinit var oneDayBeforeDateString: String
    private lateinit var oneMonthBeforeDateString: String
    private lateinit var oneYearBeforeDateString: String
    private var timeSpan: String = "month"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChartsBinding.inflate(inflater, container, false)

        val polygonApiService = PolygonApiService.getInstance(requireContext())
        val polygonRepository = PolygonRepository(polygonApiService)
        viewModel = ViewModelProvider(
            this,
            PolyGonViewModelFactory(polygonRepository)
        )[PolygonViewModel::class.java]

        getTickers("fx", true, 1000, apiKey)

        binding.btnDay.setOnClickListener {
            timeSpan = "day"
            updateViews(binding.btnDay)
        }
        binding.btnMonth.setOnClickListener {
            timeSpan = "month"
            updateViews(binding.btnMonth)
        }
        binding.btnYear.setOnClickListener {
            timeSpan = "year"
            updateViews(binding.btnYear)
        }
        getDates()
        observeForexData()

        return binding.root
    }

    private fun observeForexData() {
        viewModel.forexDataResponse.observe(viewLifecycleOwner) {
            Log.d(TAG, "Response: $it")
            if (isAdded) {
                it?.let {
                    val forexList = ArrayList<Float>()
                    if (it.results.isNotEmpty()) {
                        for(forex in it.results){
                            forexList.add(forex.vw.toFloat())
                        }
                        setUpChart(forexList)
                    }
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (isAdded) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setUpChart(forexList: ArrayList<Float>) {
        binding.llTime.visibility = View.VISIBLE
        binding.cardChart.visibility = View.VISIBLE
        binding.lineChartView.setShowLineChartPoint(true)
        binding.lineChartView.setLimitArray(forexList)
        binding.lineChartView.setPointXInit(0, 8)
        val pointList = ArrayList<PointInfo>()
        (1..forexList.size).forEach { i ->
            pointList.add(PointInfo(i.toFloat(), forexList[i-1]))
        }
        val xAxisList = pointList.map { pointInfo ->
            AxisInfo(pointInfo.x, pointInfo.x.toInt().toString())
        }
        val yAxisList = ArrayList<AxisInfo>()
        (1..forexList.size).forEach { i ->
            yAxisList.add(AxisInfo(forexList[i-1], forexList[i-1].toString()))
        }
        binding.lineChartView.setData(pointList, xAxisList=xAxisList, yAxisList=yAxisList, pointSpace = 60f)

    }

    private fun updateViews(view: TextView) {
        binding.btnDay.setBackgroundResource(R.drawable.btn_time_normal)
        binding.btnDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.btnMonth.setBackgroundResource(R.drawable.btn_time_normal)
        binding.btnMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.btnYear.setBackgroundResource(R.drawable.btn_time_normal)
        binding.btnYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        view.setBackgroundResource(R.drawable.btn_time_selected)
        view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        getDailyForexData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDates() {
        val currentDate = LocalDate.now()
        val oneDayBefore = currentDate.minusDays(1)
        val oneMonthBefore = currentDate.minusMonths(1)
        val oneYearBefore = currentDate.minusYears(1)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        currentDateString = currentDate.format(formatter)
        oneDayBeforeDateString = oneDayBefore.format(formatter)
        oneMonthBeforeDateString = oneMonthBefore.format(formatter)
        oneYearBeforeDateString = oneYearBefore.format(formatter)
    }

    private fun getTickers(market: String, active: Boolean, limit: Int, apiKey: String) {
        viewModel.tickers.observe(viewLifecycleOwner) {
            Log.d(TAG, "Response: $it")
            if (isAdded) {
                it?.let {
                    setupCurrencySymbolsSpinner(it)
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (isAdded) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.getTickers(market, active, limit, apiKey)
    }

    private fun getDailyForexData() {
        when (timeSpan) {
            "day" -> viewModel.getDailyForexData(
                mTicker,
                oneDayBeforeDateString,
                oneDayBeforeDateString,
                true,
                "asc",
                1,
                apiKey
            )

            "month" -> viewModel.getDailyForexData(
                mTicker,
                oneMonthBeforeDateString,
                currentDateString,
                true,
                "asc",
                30,
                apiKey
            )

            "year" -> viewModel.getDailyForexData(
                mTicker,
                oneYearBeforeDateString,
                currentDateString,
                true,
                "asc",
                365,
                apiKey
            )
        }

    }

    private fun setupCurrencySymbolsSpinner(ticker: Ticker) {
        val filteredTickersByEUR = ticker.results.filter { it.base_currency_symbol == "EUR" }
        for (ticker in filteredTickersByEUR) {
            tickers.add(ticker.ticker)
            currencySymbols.add(ticker.currency_symbol)
        }
        val customSpinnerAdapter = CustomSpinnerAdapter(requireContext(), currencySymbols)
        binding.spCurrencySymbols.adapter = customSpinnerAdapter
        binding.spCurrencySymbols.setSelection(currencySymbols.indexOf("USD"))
        binding.spCurrencySymbols.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    l: Long
                ) {
                    mTicker = tickers[position]
                    getDailyForexData()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
    }

    inner class CustomSpinnerAdapter(context: Context, items: List<String>) :
        ArrayAdapter<String>(context, R.layout.custom_spinner_item, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createCustomView(position, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createCustomView(position, parent)
        }

        private fun createCustomView(position: Int, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.custom_spinner_item, parent, false)

            val spinnerText = view.findViewById<TextView>(R.id.spinnerText)
            spinnerText.text = getItem(position)

            return view
        }
    }

    companion object {

        private val TAG: String = "ForexDaily"
    }
}