package com.example.currancyexchangeapi.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currancyexchangeapi.R

class RatesAdapter(
    private val currenciesList: List<String>,
    private val ratesList: List<Double>,
    private val amount: Int,
) : RecyclerView.Adapter<RatesAdapter.FavoritesViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rates_list_row_item, parent, false)
        return FavoritesViewHolder(view)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(
        holder: FavoritesViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val currency = currenciesList[position]
        val rate = ratesList[position]
        holder.tvCurrency.text = currency
        holder.tvConversion.text = String.format("%.2f", rate * amount)
        holder.tvFromBase.text = "1 EUR = ${String.format("%.4f", rate)} $currency"
        holder.tvToBase.text = "1 $currency = ${String.format("%.4f", 1 / rate)} EUR"

    }

    override fun getItemCount(): Int {
        return currenciesList.size
    }

    inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCurrency: TextView
        var tvConversion: TextView
        var tvFromBase: TextView
        var tvToBase: TextView

        init {
            tvCurrency = itemView.findViewById(R.id.tv_currency)
            tvConversion = itemView.findViewById(R.id.tv_conversion)
            tvFromBase = itemView.findViewById(R.id.tv_from_base)
            tvToBase = itemView.findViewById(R.id.tv_to_base)
        }
    }
}