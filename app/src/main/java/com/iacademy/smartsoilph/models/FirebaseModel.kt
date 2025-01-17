package com.iacademy.smartsoilph.models

//import datamodels
import android.content.Context
import android.util.Log
import com.iacademy.smartsoilph.datamodels.UserData
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.datamodels.RecommendationData

//import database
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iacademy.smartsoilph.datamodels.RequiredFertilizerData

class FirebaseModel {

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
        val referenceDetails = referenceUser.child("RecommendationHistory").child(recommendationData.recommendationID) // Reference ID

        // Create and save recommendation data
        referenceDetails.setValue(recommendationData.apply { this.isSavedOnline = true }) // Mark as saved online
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
                    val dataList = snapshot.children.mapNotNull { it.getValue(RecommendationData::class.java) }
                    onDataReceived(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseModel", "Error fetching recommendation history: ${error.message}")
                }
            })
        }
    }
    

    fun getAllUserDataFromFirebase(auth: FirebaseAuth, context: Context) {
        val userId = auth.currentUser?.uid ?: return
        val firebaseDB = Firebase.database.reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(userId)
        val dbHelper = SQLiteModel(context)

        // Fetch and save user details
        // Fetch and save user details
        referenceUser.child("UserDetails").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val email = snapshot.child("email").value.toString()
                val number = snapshot.child("number").value.toString().toDoubleOrNull() ?: 0.0
                val userData = UserData(userId, name, email, number)
                dbHelper.addUserData(userData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseModel", "Failed to fetch user details: ${error.message}")
            }
        })


        // Fetch and save recommendation data
        referenceUser.child("RecommendationHistory").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val recommendationID = child.key ?: return

                    // Extract nested soil data
                    val soilSnapshot = child.child("soilData")
                    val cropType = soilSnapshot.child("cropType").value.toString()
                    val nitrogen = soilSnapshot.child("nitrogen").value.toString().toFloatOrNull() ?: 0f
                    val phosphorus = soilSnapshot.child("phosphorus").value.toString().toFloatOrNull() ?: 0f
                    val potassium = soilSnapshot.child("potassium").value.toString().toFloatOrNull() ?: 0f
                    val phLevel = soilSnapshot.child("phLevel").value.toString().toFloatOrNull() ?: 0f
                    val ecLevel = soilSnapshot.child("ecLevel").value.toString().toFloatOrNull() ?: 0f
                    val humidity = soilSnapshot.child("humidity").value.toString().toFloatOrNull() ?: 0f
                    val temperature = soilSnapshot.child("temperature").value.toString().toFloatOrNull() ?: 0f
                    val soilTexture = soilSnapshot.child("soilTexture").value.toString()

                    // Extract nested required fertilizer data
                    val requiredFertilizerSnapshot = child.child("requiredFertilizerData")
                    val requiredN = soilSnapshot.child("requiredN").value.toString().toFloatOrNull() ?: 0f
                    val requiredP = soilSnapshot.child("requiredP").value.toString().toFloatOrNull() ?: 0f
                    val requiredK = soilSnapshot.child("requiredK").value.toString().toFloatOrNull() ?: 0f
                    val fertilizer1 = soilSnapshot.child("fertilizer1").value.toString()
                    val fertilizer2 = soilSnapshot.child("fertilizer2").value.toString()
                    val fertilizer3 = soilSnapshot.child("fertilizer3").value.toString()
                    val kgFertilizer1 = soilSnapshot.child("kgFertilizer1").value.toString().toFloatOrNull() ?: 0f
                    val kgFertilizer2 = soilSnapshot.child("kgFertilizer2").value.toString().toFloatOrNull() ?: 0f
                    val kgFertilizer3 = soilSnapshot.child("kgFertilizer3").value.toString().toFloatOrNull() ?: 0f
                    val bagFertilizer1 = soilSnapshot.child("bagFertilizer1").value.toString().toFloatOrNull() ?: 0f
                    val bagFertilizer2 = soilSnapshot.child("bagFertilizer2").value.toString().toFloatOrNull() ?: 0f
                    val bagFertilizer3 = soilSnapshot.child("bagFertilizer3").value.toString().toFloatOrNull() ?: 0f

                    // Extract nested soil data
                    val dateOfRecommendation = child.child("dateOfRecommendation").value.toString()
                    val initialStorageType = child.child("initialStorageType").value.toString()
                    val isSavedOnline = true

                    Log.d("", "recommendationID: $recommendationID")
                    Log.d("", "soilData: $nitrogen")
                    Log.d("", "fertilizer1: $fertilizer1")
                    Log.d("", "KG fertilizer1: $kgFertilizer1")
                    Log.d("", "dateOfRecommendation: $dateOfRecommendation")
                    Log.d("", "storageType: $initialStorageType")
                    Log.d("", "savedOnline: $isSavedOnline")

                    // Constructing SoilData object
                    val soilData = SoilData(
                        cropType, nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature, soilTexture
                    )

                    // Constructing RequiredFertilizerData object
                    val requiredFertilizerData = RequiredFertilizerData(
                        requiredN, requiredP, requiredK,
                        fertilizer1, fertilizer2, fertilizer3,
                        kgFertilizer1, kgFertilizer2, kgFertilizer3,
                        bagFertilizer1, bagFertilizer2, bagFertilizer3
                    )


                    val recommendationData = RecommendationData(
                        recommendationID, userId, soilData, requiredFertilizerData,
                        dateOfRecommendation, initialStorageType, isSavedOnline
                    )

                    dbHelper.addSoilData(recommendationData) // Assuming a method to save individual recommendation data
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseModel", "Failed to fetch recommendation history: ${error.message}")
            }
        })
    }




}