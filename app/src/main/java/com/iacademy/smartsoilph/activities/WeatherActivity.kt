package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.databinding.ActivityWeatherBinding
import com.iacademy.smartsoilph.weather.DailyForecast
import com.iacademy.smartsoilph.weather.DailyWeather
import com.iacademy.smartsoilph.weather.WeatherResponse
import com.iacademy.smartsoilph.weather.OpenMeteoAPIService
import com.iacademy.smartsoilph.weather.RetrofitClient
import com.iacademy.smartsoilph.weather.WeatherForecastAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : BaseActivity() {

    //declare layout variables
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private lateinit var forecastAdapter: WeatherForecastAdapter
    private lateinit var rvWeatherForecast: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeRecyclerView()

        checkPermissions()
        initializeReturnButton()
    }

    /*********************************
     * A. Initializing RecyclerView
     *-------------------------------*/
    private fun initializeRecyclerView() {
        // initialize recyclerview
        rvWeatherForecast = findViewById(R.id.rv_weather)

        // initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /*********************************
     * B. Get Date
     *-------------------------------*/
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

    private fun getCityName(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1)!!
            if (addresses.isNotEmpty()) {
                return addresses[0].locality ?: "Unknown Location"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "Unknown Location"
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        val service = RetrofitClient.instance.create(OpenMeteoAPIService::class.java)
        val call = service.getWeatherForecast(latitude, longitude)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("WeatherActivity", "API Response Success: ${response.body()}")
                    val weatherData = response.body()
                    weatherData?.daily?.let { dailyForecast ->
                        runOnUiThread {
                            updateUIWithWeatherData(weatherData, dailyForecast, latitude, longitude)
                        }
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

    private fun updateUIWithWeatherData(weatherData: WeatherResponse, dailyForecast: DailyForecast, latitude: Double, longitude: Double) {
        weatherData.current?.let {

            val temperature = it.temperature.toInt() // Convert to Int
            val humidity = it.humidity.toInt()       // Convert to Int
            val windSpeed = it.windSpeed.toInt()     // Convert to Int
            val precipitation = it.precipitation.toInt() // Convert to Int
            val currentDate = getCurrentFormattedDate()
            val location = getCityName(latitude, longitude)

            binding.tvTemperatureNumber.text = "$temperature"
            binding.tvValueHumidity.text = "$humidity%"
            binding.tvValueWind.text = "${windSpeed}km/h"
            binding.tvValuePrecipitation.text = "$precipitation%"
            binding.tvDate.text = "$currentDate"
            binding.tvLocation.text = "$location"
        }

        weatherData.current?.weatherCode?.let {
            val weatherCondition = interpretWeatherCode(it)
            binding.tvWeather.text = weatherCondition
            binding.ivWeatherIcon.setImageResource(getWeatherIcon(it))

            val forecasts = dailyForecast.time.zip(dailyForecast.maxTemperature).map { (time, temp) ->
                    DailyWeather(time, temp, it)
                }

            forecastAdapter = WeatherForecastAdapter(forecasts)
            rvWeatherForecast.adapter = forecastAdapter
            rvWeatherForecast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
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

    private fun getWeatherIcon(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.drawable.img_sun
            1, 2, 3 -> R.drawable.img_partly_cloudy
            61, 63, 65 -> R.drawable.img_rain
            95, 96, 99 -> R.drawable.img_thunderstorm
            else -> R.drawable.img_weather
        }
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
}
