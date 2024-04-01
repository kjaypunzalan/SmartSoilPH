package com.iacademy.smartsoilph.weather

data class DailyWeatherResponse(
    val time: String,
    val maxTemperature: Double,
    val weatherCode: Int
)