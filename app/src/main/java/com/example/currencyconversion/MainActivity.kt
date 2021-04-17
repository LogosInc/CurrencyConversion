package com.example.currencyconversion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.currencyconversion.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinnerSetup()
        textChanged()
    }

    private fun textChanged() {
        binding.etFirstConversion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }
        })
    }

    private fun getApiResult() {
        if (binding.etFirstConversion.text.isNotEmpty() && binding.etFirstConversion.text.isNotBlank()) {

            val API = "https://api.ratesapi.io/api/latest?base=$baseCurrency&symbols=$convertedToCurrency"

            if (baseCurrency == convertedToCurrency) {
                Toast.makeText(applicationContext, "Cannot convert the same currency", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)

                        conversionRate = jsonObject.getJSONObject("rates").getString(convertedToCurrency).toFloat()

                        Log.d("Main", "$conversionRate")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main) {
                            val text = ((binding.etFirstConversion.text.toString().toFloat()) * conversionRate).toString()
                            binding.etSecondConversion?.setText(text)

                        }
                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    }

    private fun spinnerSetup() {
        val spinner: Spinner = binding.spinnerFirstConversion
        val spinner2: Spinner = binding.spinnerSecondConversion

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }
}