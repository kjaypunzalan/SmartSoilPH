package com.iacademy.smartsoilph.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.DatabaseHelper
import com.iacademy.smartsoilph.models.FirebaseModel

class HomeActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var btnSoil: CardView
    private lateinit var btnWeather: CardView
    private lateinit var btnReports: CardView
    private lateinit var btnManual: CardView
    private lateinit var tvUsername: TextView
    private lateinit var btnBluetooth: CardView

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // declare + initialize variables
        btnSoil = findViewById<CardView>(R.id.soil_card);
        btnWeather = findViewById<CardView>(R.id.weather_card);
        btnReports = findViewById<CardView>(R.id.reports_card);
        btnManual = findViewById<CardView>(R.id.manual_card);
        tvUsername = findViewById<TextView>(R.id.tv_username)
        btnBluetooth = findViewById<CardView>(R.id.bluetooth_card)

        // set click listeners for buttons
        setButtonClickListener(btnSoil, SoilActivity::class.java)
        setButtonClickListener(btnWeather, WeatherActivity::class.java)
        setButtonClickListener(btnReports, ReportsActivity::class.java)
        setButtonClickListener(btnManual, RecommendationHistoryActivity::class.java)
        setButtonClickListener(btnBluetooth, ArduinoController::class.java)

        fetchUsername()
    }

    //Button Function
    private fun setButtonClickListener(button: CardView, activityClass: Class<*>) {
        button.setOnClickListener {
            val intent = Intent(this, activityClass)
            startActivity(intent)
        }
    }

    private fun fetchUsername() {
        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.getReference("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)

        //Get User and Soil Reference
        val userReference = firebaseDB.child("UserDetails")
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(FirebaseModel::class.java) ?: return

                //put UserName to TextView
                tvUsername.text = " " + data.name.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetDatabase() {
        val dbHelper = DatabaseHelper(this)
        dbHelper.deleteDatabase()
        // Show a Toast message confirming the database reset
        Toast.makeText(this, "Database has been reset", Toast.LENGTH_SHORT).show()
    }
}