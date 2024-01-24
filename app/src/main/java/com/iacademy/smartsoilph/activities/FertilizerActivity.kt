package com.iacademy.smartsoilph.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.FirebaseModel

class FertilizerActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var tvUserName: TextView
    private lateinit var tvFertilizerAmount: TextView
    private lateinit var tvFertilizerAmount1: TextView
    private lateinit var tvLimeAmount: TextView
    private lateinit var tvLimeAmount1: TextView
    private lateinit var btnPreviousRecommendations: Button
    private lateinit var btnReturnSoil: Button

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fertilizer)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        tvUserName = findViewById<TextView>(R.id.tv_user_greeting)
        tvFertilizerAmount = findViewById<TextView>(R.id.tv_fertilizer_amount)
        tvFertilizerAmount1 = findViewById<TextView>(R.id.tv_fertilizer_amount1)
        tvLimeAmount = findViewById<TextView>(R.id.tv_lime_amount)
        tvLimeAmount1 = findViewById<TextView>(R.id.tv_lime_amount1)
        btnPreviousRecommendations = findViewById<Button>(R.id.btn_previous);
        btnReturnSoil = findViewById<Button>(R.id.btn_return_soil);

        //Buttons
        btnReturnSoil.setOnClickListener {
            val Intent = Intent(this, SoilActivity::class.java);
            startActivity(Intent);
        }

        //Initialize Content
        initializeContent()
    }

    private fun initializeContent() {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.getReference("SmartSoilPH")
            .child("Users")
            .child(auth.currentUser!!.uid)

        //Get User and Soil Reference
        val userReference = firebaseDB.child("UserDetails")
        val soilReference = firebaseDB.child("SoilDetails")

        //Get Firebase User Data
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(FirebaseModel::class.java) ?: return

                //put UserName to TextView
                tvUserName.text = " " + data.name.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
        })

        //Get Firebase Soil Data
        soilReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(FirebaseModel::class.java) ?: return

                //format recommended fertilizer
                val formattedWeight = String.format("%.1f", data.fertilizerRecommendation)

                //put to text
                tvFertilizerAmount.text = formattedWeight.toString()
                tvFertilizerAmount1.text = formattedWeight.toString()
                tvLimeAmount.text = data.limeRecommendation.toString()
                tvLimeAmount1.text = data.limeRecommendation.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}