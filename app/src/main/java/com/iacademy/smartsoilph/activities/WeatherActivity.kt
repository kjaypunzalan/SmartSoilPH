package com.iacademy.smartsoilph.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iacademy.smartsoilph.databinding.ActivityWeatherBinding
import com.iacademy.smartsoilph.datamodels.CurrentConditionsResponse
import com.iacademy.smartsoilph.datamodels.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationKey = "262269" // Replace with actual location key
        fetchCurrentConditions(locationKey)
        fetchDailyForecast(locationKey)
    }

    private fun fetchCurrentConditions(locationKey: String) {
        val weatherService = RetrofitClient.instance.create(AccuWeatherAPIService::class.java)
        val call = weatherService.getCurrentConditions(locationKey, "sp1ubpYQWiAR50k9Gja5or9OEX6lmAUI")

        call.enqueue(object : Callback<List<CurrentConditionsResponse>> {
            override fun onResponse(
                call: Call<List<CurrentConditionsResponse>>,
                response: Response<List<CurrentConditionsResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.firstOrNull()?.let {
                        runOnUiThread {
                            binding.tvTemperature.text = "${it.Temperature.Metric.Value} ${it.Temperature.Metric.Unit}"
                            binding.tvWeather.text = it.WeatherText
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<CurrentConditionsResponse>>, t: Throwable) {
                Log.e("WeatherApp", "Error fetching current weather data", t)
            }
        })
    }

    private fun fetchDailyForecast(locationKey: String) {
        val weatherService = RetrofitClient.instance.create(AccuWeatherAPIService::class.java)
        val call = weatherService.getDailyForecast(locationKey, "sp1ubpYQWiAR50k9Gja5or9OEX6lmAUI")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.DailyForecasts?.firstOrNull()?.let { forecast ->
                        runOnUiThread {
                            binding.tvValueTemp.text = "Max: ${forecast.Temperature.Maximum.Value} ${forecast.Temperature.Maximum.Unit}"
                            // You can add more UI updates here based on the forecast data
                        }
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherApp", "Error fetching daily forecast data", t)
            }
        })
    }
}
