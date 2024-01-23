package com.iacademy.smartsoilph.datamodels

import com.google.gson.annotations.SerializedName

// Main response class
data class WeatherResponse(
    val current: CurrentWeather?,
    val hourly: HourlyWeather?,
    val daily: DailyWeather?
)

// Current weather data
data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Double,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("weather_code") val weatherCode: Int
)

// Hourly weather data (only including wind speed for simplicity)
data class HourlyWeather(
    val windSpeed80m: List<Double> // Assuming it's a list of wind speeds
)

// Daily weather data (only including max temperature for simplicity)
data class DailyWeather(
    @SerializedName("temperature_2m_max") val maxTemperature: List<Double> // Assuming it's a list of max temperatures
)
