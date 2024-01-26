package com.iacademy.smartsoilph.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.CheckInternet

class SoilActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var etNitrogen: EditText
    private lateinit var etPhosphorus: EditText
    private lateinit var etPotassium: EditText
    private lateinit var etPHLevel: EditText
    private lateinit var etECLevel: EditText
    private lateinit var etHumidity: EditText
    private lateinit var etTemperature: EditText
    private lateinit var btnViewRecommendation: Button

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        etNitrogen = findViewById<EditText>(R.id.nitrogen_value);
        etPhosphorus = findViewById<EditText>(R.id.phosphorus_value);
        etPotassium = findViewById<EditText>(R.id.potassium_value);
        etPHLevel = findViewById<EditText>(R.id.ph_level_value);
        etECLevel = findViewById<EditText>(R.id.ec_level_value);
        etHumidity = findViewById<EditText>(R.id.humidity_soil_value);
        etTemperature = findViewById<EditText>(R.id.temp_soil_value);
        btnViewRecommendation = findViewById<Button>(R.id.btn_view_fertilizer);

        // View Recommendation Button
        btnViewRecommendation.setOnClickListener {

            // Get values from EditText fields
            val nitrogen = etNitrogen.text.toString().toDoubleOrNull() ?: 0.0
            val phosphorus = etPhosphorus.text.toString().toDoubleOrNull() ?: 0.0
            val potassium = etPotassium.text.toString().toDoubleOrNull() ?: 0.0
            val phLevel = etPHLevel.text.toString().toDoubleOrNull() ?: 0.0
            val ecLevel = etECLevel.text.toString().toDoubleOrNull() ?: 0.0
            val humidity = etHumidity.text.toString().toDoubleOrNull() ?: 0.0
            val temperature = etTemperature.text.toString().toDoubleOrNull() ?: 0.0

            /******************************
             * Computations
             * ---------------------------*/
            /* SOIL NPK COMPUTATIONS */
            // Calculate fertilizer application rate
            val desiredNApplicationRate = 43.0 // Example N application rate in lbs/acre
            val fertilizerApplicationRate =
                desiredNApplicationRate / ((nitrogen / 100.0).takeIf { it > 0 } ?: 1.0)

            // Calculate fertilizer weight required for 1 acre
            val lawnArea = 1.0 // Example lawn area in acres
            val fertilizerWeightRequired = fertilizerApplicationRate * lawnArea
            val fertilizerWeightKg = fertilizerWeightRequired * 0.453592 // Convert weight to kilograms

            /* LIME COMPUTATIONS */
            // Calculate lime requirement
            val targetPH = 5.5 // Example target pH
            val currentPH = phLevel
            val soilTextureFactor =
                when {
                    currentPH >= 5.5 -> 0.0 // No lime needed if current pH is already above target
                    phLevel <= 7.0 -> 4.0 // Loam to clay loam
                    phLevel <= 8.0 -> 3.0 // Sandy loam
                    else -> 2.0 // Sand
                }
            val limeRequirement = (targetPH - currentPH) * soilTextureFactor

            /***************************
            * Check Internet Connectivity
            * ---------------------------*/
            val checkInternet = CheckInternet(this)
            if (checkInternet.isInternetAvailable()) {
                // Internet is available
                // Add to Firebase Database
                FirebaseModel().saveSoilData(nitrogen, potassium, phosphorus, phLevel, ecLevel, humidity, temperature, fertilizerWeightKg, limeRequirement, auth)
                FirebaseModel().saveRecommendation(0, fertilizerWeightKg, limeRequirement, auth)
            } else {
                // Internet is not available

            }

            /***************************
             * Go to FertilizerActivity
             * ---------------------------*/
            // Pass the computed value to the FertilizerActivity
            val intent = Intent(this, FertilizerActivity::class.java)
            startActivity(intent)
        }

    }
}