package com.iacademy.smartsoilph.datamodels

data class WeatherResponse(
    val DailyForecasts: List<DailyForecast>
    // Add other top-level fields from the AccuWeather response if necessary
)

data class DailyForecast(
    val Date: String,
    val Temperature: Temperature,
    val Day: DayForecast,
    val Night: NightForecast
    // Add other fields as per the AccuWeather daily forecast response
)

data class Temperature(
    val Minimum: TemperatureDetail,
    val Maximum: TemperatureDetail
    // Add other fields if necessary
)

data class TemperatureDetail(
    val Value: Double,
    val Unit: String
    // Add other fields if necessary
)

data class DayForecast(
    val Icon: Int,
    val IconPhrase: String,
    val WeatherCondition: String // Field to store the weather condition (e.g., "Cloudy")
    // Add other fields if necessary
)

data class NightForecast(
    val Icon: Int,
    val IconPhrase: String,
    val WeatherCondition: String // Field to store the weather condition (e.g., "Clear")
    // Add other fields if necessary
)
