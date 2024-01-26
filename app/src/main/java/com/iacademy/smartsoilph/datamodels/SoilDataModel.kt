package com.iacademy.smartsoilph.datamodels

class   SoilDataModel {

    //Data Types
    var nitrogen: Float = 0.0F
    var phosphorus: Float = 0.0F
    var potassium: Float = 0.0F
    var phLevel: Float = 0.0F
    var ecLevel: Float = 0.0F
    var humidity: Float = 0.0F
    var temperature: Float = 0.0F

    var fertilizerRecommendation: Float = 0.0F
    var limeRecommendation: Float = 0.0F

    //Constructors
    constructor()
    constructor(nitrogen: Float, phosphorus: Float, potassium: Float,
        phLevel: Float, ecLevel: Float,
        humidity: Float, temperature: Float) {
        this.nitrogen = nitrogen
        this.phosphorus = phosphorus
        this.potassium = potassium
        this.phLevel = phLevel
        this.ecLevel = ecLevel
        this.humidity = humidity
        this.temperature = temperature
    }

}