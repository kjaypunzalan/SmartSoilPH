package com.iacademy.smartsoilph.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.iacademy.smartsoilph.R

class SoilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil)

        // declare + initialize variables
        val etNitrogen = findViewById<EditText>(R.id.nitrogen_value);
        val etPhosphorus = findViewById<EditText>(R.id.phosphorus_value);
        val etPotassium = findViewById<EditText>(R.id.potassium_value);
        val etPHLevel = findViewById<EditText>(R.id.ph_level_value);
        val etECLevel = findViewById<EditText>(R.id.ec_level_value);
        val etHumidity = findViewById<EditText>(R.id.humidity_soil_value);
        val etTemperature = findViewById<EditText>(R.id.temp_soil_value);
        val btnViewRecommendation = findViewById<Button>(R.id.btn_view_fertilizer);

        // View Recommendation Button
        btnViewRecommendation.setOnClickListener {

            // Get values from EditText fields
            val nitrogenValue = etNitrogen.text.toString().toDoubleOrNull() ?: 0.0
            val phosphorusValue = etPhosphorus.text.toString().toDoubleOrNull() ?: 0.0
            val potassiumValue = etPotassium.text.toString().toDoubleOrNull() ?: 0.0
            val phLevelValue = etPHLevel.text.toString().toDoubleOrNull() ?: 0.0
            val ecLevelValue = etECLevel.text.toString().toDoubleOrNull() ?: 0.0

            /* SOIL NPK COMPUTATIONS */
            // Calculate fertilizer application rate
            val desiredNApplicationRate = 43.0 // Example N application rate in lbs/acre
            val fertilizerApplicationRate =
                desiredNApplicationRate / ((nitrogenValue / 100.0).takeIf { it > 0 } ?: 1.0)

            // Calculate fertilizer weight required for 1 acre
            val lawnArea = 1.0 // Example lawn area in acres
            val fertilizerWeightRequired = fertilizerApplicationRate * lawnArea

            // Convert weight to kilograms
            val fertilizerWeightKg = fertilizerWeightRequired * 0.453592

            /* LIME COMPUTATIONS */
            // Calculate lime requirement
            val targetPH = 5.5 // Example target pH
            val currentPH = phLevelValue
            val soilTextureFactor =
                when {
                    currentPH >= 5.5 -> 0.0 // No lime needed if current pH is already above target
                    phLevelValue <= 7.0 -> 4.0 // Loam to clay loam
                    phLevelValue <= 8.0 -> 3.0 // Sandy loam
                    else -> 2.0 // Sand
                }
            val limeRequirement = (targetPH - currentPH) * soilTextureFactor


            // Pass the computed value to the FertilizerActivity
            val intent = Intent(this, FertilizerActivity::class.java)
            intent.putExtra("fertilizerWeight", fertilizerWeightKg)
            intent.putExtra("limeRequirement", limeRequirement)
            startActivity(intent)
        }

    }
}