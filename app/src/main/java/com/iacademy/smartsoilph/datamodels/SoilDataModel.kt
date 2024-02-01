package com.iacademy.smartsoilph.datamodels

data class UserData(
    val name: String,
    val email: String,
    val number: Double
)

data class SoilData(
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val phLevel: Float,
    val ecLevel: Float,
    val humidity: Float,
    val temperature: Float,
)

data class RecommendationData(
    val soilData: SoilData,
    val fertilizerRecommendation: Float,
    val limeRecommendation: Float,
    val dateOfRecommendation: String,
    var initialStorageType: String
)