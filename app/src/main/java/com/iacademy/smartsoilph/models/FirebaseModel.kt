package com.iacademy.smartsoilph.models

//import datamodels
import com.iacademy.smartsoilph.datamodels.UserData
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.datamodels.RecommendationData

//import database
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Calendar

class FirebaseModel {

    /**************************
     * Firebase Database data
     *------------------------*/
    //user data
    var name: String? = null
    var email: String? = null
    var number: Double? = null

    //soil data
    var nitrogen: Float? = null
    var potassium: Float? = null
    var phosphorus: Float? = null
    var phLevel: Float? = null
    var ecLevel: Float? = null
    var humidity: Float? = null
    var temperature: Float? = null

    //recommendation data
    var fertilizerBag: Int? = null
    var fertilizerRecommendation: Float? = null
    var limeRecommendation: Float? = null
    var dateOfRecommendation: String? = null
    var initialStorageType: String? = null

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
        nitrogen: Float, potassium: Float, phosphorus: Float,
        phLevel: Float, ecLevel: Float, humidity: Float, temperature: Float,
        fertilizerRecommendation: Float, limeRecommendation: Float) {
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
        nitrogen: Float, potassium: Float, phosphorus: Float,
        phLevel: Float, ecLevel: Float, humidity: Float, temperature: Float,
        fertilizerRecommendation: Float, limeRecommendation: Float,
        dateOfRecommendation: String, initialStorageType: String) {
        this.nitrogen = nitrogen
        this.potassium = potassium
        this.phosphorus = phosphorus
        this.phLevel = phLevel
        this.ecLevel = ecLevel
        this.humidity = humidity
        this.temperature = temperature
        this.fertilizerRecommendation = fertilizerRecommendation
        this.limeRecommendation = limeRecommendation
        this.dateOfRecommendation = dateOfRecommendation
        this.initialStorageType = initialStorageType
    }


    /**************************
     * Functions
     *------------------------*/
    /**************************
     * A. write new user
     *------------------------*/
    fun writeNewUser(
        userData: UserData,
        auth: FirebaseAuth
    ) {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference

        // Reference to SmartSoilPH
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("UserDetails")

        // Create and set user data
        referenceDetails.setValue(userData)
    }


    /*******************************************
     * B. save soil nutrient data to firebase
     *------------------------------------------*/
    fun saveSoilData(
        soilData: SoilData,
        auth: FirebaseAuth
    ) {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference

        // Soil data reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("SoilDetails")

        // Create and save soil data
        referenceDetails.setValue(soilData)
    }

    /*******************************************
     * C. save recommendations to firebase
     *------------------------------------------*/
    fun saveRecommendation(
        recommendationData: RecommendationData,
        auth: FirebaseAuth
    ) {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference

        // Soil data reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("RecommendationHistory").push()

        // Create and save recommendation data
        referenceDetails.setValue(recommendationData)
    }


    /*******************************************
     * D. fetch recommendation history in recycler view
     *------------------------------------------*/
    companion object {
        fun fetchRecommendationHistory(auth: FirebaseAuth, onDataReceived: (List<RecommendationData>) -> Unit) {
            val firebaseDB = Firebase.database.reference
            val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
            val referenceDetails = referenceUser.child("RecommendationHistory")

            referenceDetails.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = snapshot.children.mapNotNull { dataSnapshot ->
                        dataSnapshot.getValue(FirebaseModel::class.java)?.let { firebaseModel ->
                            RecommendationData(
                                SoilData(
                                    firebaseModel.nitrogen ?: 0.0f,
                                    firebaseModel.potassium ?: 0.0f,
                                    firebaseModel.phosphorus ?: 0.0f,
                                    firebaseModel.phLevel ?: 0.0f,
                                    firebaseModel.ecLevel ?: 0.0f,
                                    firebaseModel.humidity ?: 0.0f,
                                    firebaseModel.temperature ?: 0.0f
                                ),
                                firebaseModel.fertilizerRecommendation ?: 0.0f,
                                firebaseModel.limeRecommendation ?: 0.0f,
                                firebaseModel.dateOfRecommendation.orEmpty(),
                                firebaseModel.initialStorageType.orEmpty()
                            )
                        }
                    }
                    onDataReceived(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error case
                }
            })
        }
    }


}