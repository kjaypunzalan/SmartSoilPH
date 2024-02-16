package com.iacademy.smartsoilph.datamodels

data class UserData(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val number: Double = 0.0
)


data class SoilData(
    val nitrogen: Float = 0f,
    val phosphorus: Float = 0f,
    val potassium: Float = 0f,
    val phLevel: Float = 0f,
    val ecLevel: Float = 0f,
    val humidity: Float = 0f,
    val temperature: Float = 0f
)


data class RecommendationData(
    val recommendationID: String = "",
    val userID: String = "",
    val soilData: SoilData = SoilData(),
    val fertilizerRecommendation: Float = 0f,
    val limeRecommendation: Float = 0f,
    val dateOfRecommendation: String = "",
    var initialStorageType: String = "",
    var isSavedOnline: Boolean = false
)
