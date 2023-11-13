package com.example.currancyexchangeapi.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.currancyexchangeapi.R
import com.example.currancyexchangeapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val ratesFragment = RatesFragment()
        fragmentTransaction.add(R.id.fragment_container, ratesFragment)
        fragmentTransaction.commit()

        binding.btnRates.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ratesFragment as Fragment).commit()

        }

        binding.btnCharts.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChartsFragment() as Fragment).commit()

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}