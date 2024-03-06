package com.iacademy.smartsoilph.datamodels

data class UserData(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val number: Double = 0.0
)


data class SoilData(
    val cropType: String = "",
    val nitrogen: Float = 0f,
    val phosphorus: Float = 0f,
    val potassium: Float = 0f,
    val phLevel: Float = 0f,
    val ecLevel: Float = 0f,
    val humidity: Float = 0f,
    val temperature: Float = 0f,
    val soilTexture: String = ""
)


data class RecommendationData(
    val recommendationID: String = "",
    val userID: String = "",
    val soilData: SoilData = SoilData(),
    val requiredFertilizerData: RequiredFertilizerData = RequiredFertilizerData(),
    val dateOfRecommendation: String = "",
    var initialStorageType: String = "",
    var isSavedOnline: Boolean = false
)

data class RequiredFertilizerData(
    val requiredN: Float = 0f,
    val requiredP: Float = 0f,
    val requiredK: Float = 0f,
    var fertilizer1: String = "",
    var fertilizer2: String = "",
    var fertilizer3: String = "",
    var kgFertilizer1: Float = 0f,
    var kgFertilizer2: Float = 0f,
    var kgFertilizer3: Float = 0f,
    var bagFertilizer1: Float = 0f,
    var bagFertilizer2: Float = 0f,
    var bagFertilizer3: Float = 0f
)
