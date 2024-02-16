package com.iacademy.smartsoilph.datamodels

import com.google.gson.annotations.SerializedName

// Main response class
data class DailyForecastWrapper(
    @SerializedName("forecasts") val forecasts: List<DailyForecast>?
)
data class WeatherResponse(
    val current: CurrentWeather?,
    val hourly: HourlyWeather?,
    val daily: DailyForecastWrapper? // Use the wrapper class
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

// Updated Daily forecast data to include detailed 5-day forecast information
data class DailyForecast(
    @SerializedName("time") val time: String,
    @SerializedName("temperature_2m_max") val maxTemperature: Double,
    @SerializedName("temperature_2m_min") val minTemperature: Double,
    @SerializedName("precipitation_sum") val precipitationSum: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    // Add more fields as needed based on the Open-Meteo API documentation
)
