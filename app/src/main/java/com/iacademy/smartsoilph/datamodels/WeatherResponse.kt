package com.iacademy.smartsoilph.datamodels

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>
    // Add other fields as per the API response
)

data class Main(val temp: Double, val humidity: Int)
data class Weather(val description: String)