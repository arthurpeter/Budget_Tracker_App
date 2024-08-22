package com.android.budgettrackerapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.Toast
import com.android.budgettrackerapp.databinding.ActivityChangeCurrencyBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ChangeCurrencyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCurrencyBinding
    private var selectedCurrency: Currency = CurrencySettings.currency
    private var exchangeRate: Double = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangeCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setInitialText()

        binding.main.setOnClickListener {
            stopHighlight()
            binding.updateCurrencyBtn.visibility = View.GONE
        }

        binding.eurBtn.setOnClickListener {
            fetchAndDisplayExchangeRate(Currency.EUR)
            selectedCurrency = Currency.EUR
            highlightSelectedCurrency()
            binding.updateCurrencyBtn.visibility = View.VISIBLE
        }

        binding.usdBtn.setOnClickListener {
            fetchAndDisplayExchangeRate(Currency.USD)
            selectedCurrency = Currency.USD
            highlightSelectedCurrency()
            binding.updateCurrencyBtn.visibility = View.VISIBLE
        }

        binding.ronBtn.setOnClickListener {
            fetchAndDisplayExchangeRate(Currency.RON)
            selectedCurrency = Currency.RON
            highlightSelectedCurrency()
            binding.updateCurrencyBtn.visibility = View.VISIBLE
        }

        binding.updateCurrencyBtn.setOnClickListener {
            if (CurrencySettings.currency != selectedCurrency) {
                changeCurrency(selectedCurrency)
            } else {
                Toast.makeText(this, "You have selected the same currency.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun setInitialText() {
        binding.ronBtn.text = "1 RON = ? ${CurrencySettings.currency}"
        binding.eurBtn.text = "1 EUR = ? ${CurrencySettings.currency}"
        binding.usdBtn.text = "1 USD = ? ${CurrencySettings.currency}"
    }

    private fun highlightSelectedCurrency() {
        binding.eurBtn.isSelected = (selectedCurrency == Currency.EUR)
        binding.usdBtn.isSelected = (selectedCurrency == Currency.USD)
        binding.ronBtn.isSelected = (selectedCurrency == Currency.RON)
    }

    private fun stopHighlight() {
        binding.eurBtn.isSelected = false
        binding.usdBtn.isSelected = false
        binding.ronBtn.isSelected = false
    }

    private fun changeCurrency(currency: Currency) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            val transactions = db.transactionDao().getAll()
            for (transaction in transactions) {
                transaction.amount /= exchangeRate
                db.transactionDao().update(transaction)
            }
            CurrencySettings.currency = selectedCurrency
            finish()
        }
    }

    data class ExchangeRateResponse(
        val success: Boolean,
        val terms: String,
        val privacy: String,
        val timestamp: Long,
        val date: String,
        val base: String,
        val rates: Map<String, Double>
    )

    private suspend fun getExchangeRate(from: Currency, to: Currency): Double {
        val apiUrl = "https://api.fxratesapi.com/latest"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(apiUrl)
            .build()
        return withContext(Dispatchers.IO) {
            // Execute the request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                // Parse the JSON response using Moshi
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter = moshi.adapter(ExchangeRateResponse::class.java)
                val exchangeRateResponse = jsonAdapter.fromJson(response.body?.string() ?: "")

                if (exchangeRateResponse?.success == true) {
                    val fromRate = exchangeRateResponse.rates[from.name] ?: 1.0
                    val toRate = exchangeRateResponse.rates[to.name] ?: 1.0
                    return@withContext toRate / fromRate
                } else {
                    throw Exception("Failed to fetch exchange rates")
                }
            }
        }
    }

    private fun fetchAndDisplayExchangeRate(from: Currency) {
        GlobalScope.launch {
            try {
                exchangeRate = getExchangeRate(from, CurrencySettings.currency)
                val formattedRate = String.format("%.2f", exchangeRate)
                withContext(Dispatchers.Main) {
                    when (from) {
                        Currency.EUR -> binding.eurBtn.text = "1 EUR = $formattedRate ${CurrencySettings.currency}"
                        Currency.USD -> binding.usdBtn.text = "1 USD = $formattedRate ${CurrencySettings.currency}"
                        Currency.RON -> binding.ronBtn.text = "1 RON = $formattedRate ${CurrencySettings.currency}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChangeCurrencyActivity, "Error fetching exchange rate: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}