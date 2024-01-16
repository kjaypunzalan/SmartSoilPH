package com.iacademy.smartsoilph.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iacademy.smartsoilph.databinding.ActivityWeatherBinding
import com.iacademy.smartsoilph.datamodels.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.iacademy.smartsoilph.activities.RetrofitClient // Ensure this import is correct
import com.iacademy.smartsoilph.activities.WeatherbitAPIService


class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding // Declare a binding object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater) // Inflate the layout
        setContentView(binding.root) // Set the content view to the root of the binding class

        fetchWeatherData(13.661158, 121.371904)
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        val weatherService = RetrofitClient.instance.create(WeatherbitAPIService::class.java)
        val call = weatherService.getDailyForecast(14.4791, 120.8970, "28511079036140e692b429d9ea3f64b9")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    weatherData?.data?.firstOrNull()?.let { dailyForecast ->
                        // Use the data
                        val highTemp = dailyForecast.max_temp
                        val curTemp = dailyForecast.temp
                        // Use highTemp and lowTemp as needed, e.g., updating the UI
                        // Example: Update a TextView with high and low temperatures
                        runOnUiThread {
                            binding.tvValueTemp.text = "High: ${highTemp}°C"
                            binding.tvTemperature.text = "${curTemp}°C"
                        }
                    }
                }
                else {
                    Log.e("WeatherApp", "Unsuccessful response: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherApp", "Error fetching weather data", t)
            }
        })

    }

}