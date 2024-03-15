package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.databinding.ActivityWeatherBinding
import com.iacademy.smartsoilph.datamodels.DailyForecast
import com.iacademy.smartsoilph.datamodels.WeatherResponse
import com.iacademy.smartsoilph.utils.OpenMeteoAPIService
import com.iacademy.smartsoilph.utils.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : BaseActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.rv_weather) // Ensure you have a RecyclerView in your layout
        recyclerView.layoutManager = LinearLayoutManager(this)

        forecastAdapter = ForecastAdapter(emptyList())
        recyclerView.adapter = forecastAdapter

        initializeReturnButton()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermissions()
    }

    private fun initializeReturnButton() {
        val btnReturn: ImageView = findViewById(R.id.toolbar_back_icon)
        btnReturn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQ_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    getWeatherData(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        val service = RetrofitClient.instance.create(OpenMeteoAPIService::class.java)
        val call = service.getWeatherForecast(
            latitude = latitude,
            longitude = longitude,
            current = "temperature_2m,relative_humidity_2m,wind_speed_10m",
            hourly = "wind_speed_80m",
            daily = "temperature_2m_max",
            timezone = "Asia/Singapore"
        )

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherActivity", "API Response Success: ${response.body()}")
                    val weatherData = response.body()!!
                    runOnUiThread {
                        // Update UI with the weather data
                        updateUIWithWeatherData(weatherData)
                    }
                } else {
                    Log.e("WeatherActivity", "Response not successful")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherActivity", "API call failed", t)
            }
        })
    }

    private fun updateUIWithWeatherData(weatherData: WeatherResponse) {
        weatherData.current?.let {
            val temperature = it.temperature.toInt() // Convert to Int
            val humidity = it.humidity.toInt()       // Convert to Int
            val windSpeed = it.windSpeed.toInt()     // Convert to Int
            val currentDate = getCurrentFormattedDate()
            val maxTemperatureToday = weatherData.daily?.forecasts?.find { forecast ->
                forecast.time.startsWith(currentDate)
            }?.maxTemperature?.toInt() ?: temperature
            // Assuming maxTemperature calculation is moved or adjusted as necessary
            binding.tvTemperatureNumber.text = "$temperature"
            binding.tvValueHumidity.text = "$humidity%"
            binding.tvValueWind.text = "${windSpeed}km/h"
            binding.tvValueTemp.text = "$maxTemperatureToday°C"
        }

        weatherData.current?.weatherCode?.let {
            val weatherCondition = interpretWeatherCode(it)
            binding.tvWeather.text = weatherCondition
        }

        // Adjusted to access the forecasts within DailyForecastWrapper
        val forecasts = weatherData.daily?.forecasts?.map { dailyForecast ->
            DailyForecast(
                time = dailyForecast.time,
                maxTemperature = dailyForecast.maxTemperature,
                minTemperature = dailyForecast.minTemperature,
                precipitationSum = dailyForecast.precipitationSum,
                weatherCode = dailyForecast.weatherCode
            )
        } ?: listOf()

        forecastAdapter = ForecastAdapter(forecasts)
        recyclerView.adapter = forecastAdapter
    }

    private fun interpretWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            56, 57 -> "Freezing Drizzle"
            61, 63, 65 -> "Rain"
            66, 67 -> "Freezing Rain"
            71, 73, 75 -> "Snow"
            77 -> "Snow Grains"
            80, 81, 82 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Unknown"
        }
    }
    // Additional methods and logic for your activity
}
