package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.databinding.ActivityWeatherBinding
import com.iacademy.smartsoilph.datamodels.WeatherResponse
import com.iacademy.smartsoilph.utils.OpenMeteoAPIService
import com.iacademy.smartsoilph.utils.RetrofitClient
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDate.text = getCurrentFormattedDate()

        CoroutineScope(Dispatchers.Main).launch {
            getWeatherData()
        }
    }

    private fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        return dateFormat.format(Date())
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

    private fun getWeatherData() {
        val service = RetrofitClient.instance.create(OpenMeteoAPIService::class.java)
        val call = service.getWeatherForecast(
            latitude = 13.7567,
            longitude = 121.0584,
            current = "temperature_2m,relative_humidity_2m,wind_speed_10m",
            hourly = "wind_speed_80m",
            daily = "temperature_2m_max",
            timezone = "Asia/Singapore"
        )

        call.enqueue(object : Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                response.body()?.let { weatherData ->
                    val currentWeather = weatherData.current
                    val dailyWeather = weatherData.daily

                    weatherData.current?.let {
                        val temperature = it.temperature.toInt()
                        val humidity = it.humidity.toInt()
                        val windSpeed = it.windSpeed.toInt()
                        val maxTemperature = weatherData.daily?.maxTemperature?.get(0)?.toInt()

                        binding.tvTemperatureNumber.text = "$temperature°C"
                        binding.tvValueHumidity.text = "$humidity%"
                        binding.tvValueWind.text = "${windSpeed}km/h"
                        binding.tvValueTemp.text = "$maxTemperature°C"
                    }

                    currentWeather?.let {
                        val weatherCondition = interpretWeatherCode(it.weatherCode)
                        binding.tvWeather.text = "$weatherCondition"
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {}
        })
    }
}