package com.iacademy.smartsoilph.datamodels

data class WeatherResponse(
    val data: List<DailyWeather>
    // Add other fields as per the Weatherbit API response
)

data class DailyWeather(
    val max_temp: Double, // Maximum temperature for the day
    val min_temp: Double, // Minimum temperature for the day
    val temp: Double,
    val weather: WeatherDescription
    // Add other relevant fields
)

data class WeatherDescription(
    val description: String
    // Add other fields relevant to the weather description
)

