package com.iacademy.smartsoilph.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.iacademy.smartsoilph.R

class FertilizerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fertilizer)

        //declare + initialize variables
        val tvFertilizerAmount = findViewById<TextView>(R.id.tv_fertilizer_amount)
        val tvFertilizerAmount1 = findViewById<TextView>(R.id.tv_fertilizer_amount1)
        val tvLimeAmount = findViewById<TextView>(R.id.tv_lime_amount)
        val tvLimeAmount1 = findViewById<TextView>(R.id.tv_lime_amount1)
        val btnPreviousRecommendations = findViewById<Button>(R.id.btn_previous);
        val btnReturnSoil = findViewById<Button>(R.id.btn_return_soil);

        // Retrieve intent value from SoilActivity
        val fertilizerWeight = intent.getDoubleExtra("fertilizerWeight", 0.0)
        val limeData = intent.getDoubleExtra("limeRequirement", 0.0)

        // Format the weight to display only one decimal place
        val formattedWeight = String.format("%.1f", fertilizerWeight)

        // Set the value in the TextView
        tvFertilizerAmount.text = " $formattedWeight kg"
        tvFertilizerAmount1.text = " $formattedWeight kg"
        tvLimeAmount.text = " $limeData tons"
        tvLimeAmount1.text = " $limeData tons"

        //Buttons
        btnReturnSoil.setOnClickListener {
            val Intent = Intent(this, SoilActivity::class.java);
            startActivity(Intent);
        }

    }
}