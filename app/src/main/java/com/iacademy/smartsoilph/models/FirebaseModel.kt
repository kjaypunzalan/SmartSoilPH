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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Calendar

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
                    val dataList = snapshot.children.mapNotNull { dataSnapshot ->
                        dataSnapshot.getValue(RecommendationData::class.java)?.apply {

                            /*firebaseModel -> RecommendationData(
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
                            )*/
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

    fun getCurrentUserDetailsAndSaveLocally(auth: FirebaseAuth, context: Context) {
        val userID = auth.currentUser?.uid ?: return
        val firebaseDB = Firebase.database.reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(userID)
        val referenceDetails = referenceUser.child("UserDetails")

        referenceDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val email = snapshot.child("email").value.toString()
                val number = snapshot.child("number").value.toString().toDoubleOrNull() ?: 0.0

                // Now save these details locally using SQLiteModel
                val userData = UserData(userID, name, email, number)
                val dbHelper = SQLiteModel(context)
                dbHelper.addUserData(userData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseModel", "Failed to fetch user details: ${error.message}")
            }
        })
    }



}