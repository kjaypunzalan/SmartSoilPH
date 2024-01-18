package com.iacademy.smartsoilph.datamodels

data class CurrentConditionsResponse(
    val LocalObservationDateTime: String,
    val EpochTime: Long,
    val WeatherText: String,
    val WeatherIcon: Int,
    val Temperature: CurrentTemperature
)

data class CurrentTemperature(
    val Metric: TemperatureValue,
    val Imperial: TemperatureValue
)

data class TemperatureValue(
    val Value: Double,
    val Unit: String,
    val UnitType: Int
)
