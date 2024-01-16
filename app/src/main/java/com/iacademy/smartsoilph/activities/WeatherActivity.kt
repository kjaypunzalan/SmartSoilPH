package com.iacademy.smartsoilph.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.databinding.ActivityWeatherBinding
import com.iacademy.smartsoilph.datamodels.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding // Declare a binding object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityWeatherBinding.inflate(layoutInflater) // Inflate the layout
        setContentView(binding.root) // Set the content view to the root of the binding class

        fetchWeatherData("San Juan, Batangas, PH")
    }

    private fun fetchWeatherData(city: String) {
        val weatherService = RetrofitClient.instance.create(WeatherAPIService::class.java)
        val call = weatherService.getWeatherByCity(city, "6e52e7a688208004eaeac5d1f95e9bd2")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        // Use the binding object to reference the views
                        binding.tvValueTemp.text = "${it.main.temp} °C"
                        binding.tvWeather.text = it.weather[0].description
                        // Update other UI components using binding as needed
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Handle network error
            }
        })
    }

}