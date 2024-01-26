package com.iacademy.smartsoilph.models

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class FirebaseModel {

    /**************************
     * Firebase Database data
     *------------------------*/
    //user data
    var name: String? = null
    var email: String? = null
    var number: Double? = null

    //soil data
    var nitrogen: Double? = null
    var potassium: Double? = null
    var phosphorus: Double? = null
    var phLevel: Double? = null
    var ecLevel: Double? = null
    var humidity: Double? = null
    var temperature: Double? = null

    //recommendation data
    var fertilizerBag: Int? = null
    var fertilizerRecommendation: Double? = null
    var limeRecommendation: Double? = null


    private lateinit var database: DatabaseReference

    /**************************
     * Constructors
     *------------------------*/
    // Empty constructor for Firebase
    constructor()

    // User constructor
    constructor(
        name: String, email: String, number: Double) {
        this.name = name
        this.email = email
        this.number = number
    }

    // Soil constructor
    constructor(
        nitrogen: Double, potassium: Double, phosphorus: Double,
        phLevel: Double, ecLevel: Double, humidity: Double, temperature: Double,
        fertilizerRecommendation: Double, limeRecommendation: Double) {
        this.nitrogen = nitrogen
        this.potassium = potassium
        this.phosphorus = phosphorus
        this.phLevel = phLevel
        this.ecLevel = ecLevel
        this.humidity = humidity
        this.temperature = temperature
        this.fertilizerRecommendation = fertilizerRecommendation
        this.limeRecommendation = limeRecommendation
    }

    // Recommendation constructor
    constructor(
        fertilizerBag: Int, fertilizerRecommendation: Double, limeRecommendation: Double) {
        this.fertilizerBag = fertilizerBag
        this.fertilizerRecommendation = fertilizerRecommendation
        this.limeRecommendation = limeRecommendation
    }


    /**************************
     * Functions
     *------------------------*/
    /**************************
     * A. write new user
     *------------------------*/
    fun writeNewUser(
        name: String,
        email: String,
        number: Double,
        auth: FirebaseAuth
    ) {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference

        // Reference to SmartSoilPH
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("UserDetails")

        // Create and set user data
        val userData = FirebaseModel(name, email, number)
        referenceDetails.setValue(userData)
    }


    /*******************************************
     * B. save soil nutrient data to firebase
     *------------------------------------------*/
    fun saveSoilData(
        nitrogen: Double, potassium: Double, phosphorus: Double,
        phLevel: Double, ecLevel: Double, humidity: Double, temperature: Double,
        fertilizerRecommendation: Double, limeRecommendation: Double,
        auth: FirebaseAuth
    ) {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference

        // Soil data reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("SoilDetails")

        // Create and set soil data
        val soilData = FirebaseModel(nitrogen, potassium, phosphorus, phLevel, ecLevel, humidity, temperature, fertilizerRecommendation, limeRecommendation)
        referenceDetails.setValue(soilData)
    }

    /*******************************************
     * C. save recommendations to firebase
     *------------------------------------------*/
    fun saveRecommendation(
        fertilizerBag: Int, fertilizerRecommendation: Double, limeRecommendation: Double,
        auth: FirebaseAuth
    ) {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference

        // Soil data reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("RecommendationHistory").push()

        // Create and set recommendation data
        val recommendationData = FirebaseModel(fertilizerBag, fertilizerRecommendation, limeRecommendation)
        referenceDetails.setValue(recommendationData)
    }

}