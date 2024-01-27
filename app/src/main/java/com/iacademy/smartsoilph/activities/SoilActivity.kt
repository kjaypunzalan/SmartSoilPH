package com.iacademy.smartsoilph.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.SoilDataModel
import com.iacademy.smartsoilph.models.DatabaseHelper
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.CheckInternet
import java.util.Calendar

class SoilActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var etNitrogen: EditText
    private lateinit var etPhosphorus: EditText
    private lateinit var etPotassium: EditText
    private lateinit var etPHLevel: EditText
    private lateinit var etECLevel: EditText
    private lateinit var etHumidity: EditText
    private lateinit var etTemperature: EditText
    private lateinit var btnFilter: CardView
    private lateinit var btnViewRecommendation: Button

    //declare dialog variable
    private lateinit var selectedGradeTextView: TextView
    private var isGradeSelected = false

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
        btnFilter = findViewById<CardView>(R.id.btn_filter);
        btnViewRecommendation = findViewById<Button>(R.id.btn_view_fertilizer);
        selectedGradeTextView = findViewById<TextView>(R.id.tv_selected_grade)

        // Button Logistics
        btnFilter.setOnClickListener {
            showGradeDialog()
        }
        btnViewRecommendation.setOnClickListener {
            if (isGradeSelected) {
                recommendation()
            } else {
                Toast.makeText(this, "Please select a grade first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*****************************
     * A. Show Grade Dialog
     ***************************/
    private fun showGradeDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_choose_grade)

        // Initialize RadioButtons using Loop
        val radioButtons = mutableListOf<RadioButton>()
        for (i in 1..25) {
            val resId = resources.getIdentifier("gs_$i", "id", packageName)
            radioButtons.add(dialog.findViewById(resId))
        }

        // Keep track of the currently selected RadioButton
        var selectedRadioButton: RadioButton? = null
        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                if (selectedRadioButton == it) {
                    // Uncheck if the same button is clicked again
                    selectedRadioButton?.isChecked = false
                    selectedRadioButton = null
                } else {
                    // Check new RadioButton and uncheck previous
                    selectedRadioButton?.isChecked = false
                    radioButton.isChecked = true
                    selectedRadioButton = radioButton
                }
            }
        }

        // Initialize Apply button
        val btnApply = dialog.findViewById<CardView>(R.id.btn_apply)
        btnApply.setOnClickListener {
            val selectedRadioButton = radioButtons.find { it.isChecked }
            if (selectedRadioButton != null) {
                // Mark true that a grade has been selected
                isGradeSelected = true
                val selectedGrade = selectedRadioButton.text.toString()
                selectedGradeTextView.text = "Selected Grade: $selectedGrade"
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please select a grade", Toast.LENGTH_SHORT).show()
            }
        }

        // Close Button
        val closeButton = dialog.findViewById<ImageView>(R.id.iv_close)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /*****************************
     * B. View Recommendation Button
     ***************************/
    private fun recommendation() {
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
            // Internet is available - add to Firebase Database
            FirebaseModel().saveSoilData(nitrogen, potassium, phosphorus, phLevel, ecLevel, humidity, temperature, fertilizerWeightKg, limeRequirement, auth)
            FirebaseModel().saveRecommendation(nitrogen, potassium, phosphorus, phLevel, ecLevel, humidity, temperature, fertilizerWeightKg, limeRequirement, "Cloud Firebase", auth)
        } else {
            // Internet is not available - add to SQLite
            val dateOfRecommendation = Calendar.getInstance().time.toString()

            val soilData = SoilDataModel()
            soilData.nitrogen = nitrogen.toFloat()
            soilData.phosphorus = phosphorus.toFloat()
            soilData.potassium = potassium.toFloat()
            soilData.phLevel = phLevel.toFloat()
            soilData.ecLevel = ecLevel.toFloat()
            soilData.humidity = humidity.toFloat()
            soilData.temperature = temperature.toFloat()
            soilData.fertilizerRecommendation = fertilizerWeightKg.toFloat()
            soilData.limeRecommendation = limeRequirement.toFloat()
            soilData.dateOfRecommendation = dateOfRecommendation
            soilData.initialStorageType = "Local SQLite"

            // Instantiate DatabaseHelper
            val dbHelper = DatabaseHelper(this)
            // Save the soil data to the SQLite database
            val result = dbHelper.addSoilData(soilData)
            if (result != -1L) {
                // Data saved successfully
                Toast.makeText(this, "Soil data saved locally", Toast.LENGTH_SHORT).show()
            } else {
                // Error occurred in saving data
                Toast.makeText(this, "Failed to save soil data locally", Toast.LENGTH_SHORT).show()
            }

        }

        /***************************
         * Go to FertilizerActivity
         * ---------------------------*/
        // Pass the computed value to the FertilizerActivity
        val intent = Intent(this, FertilizerActivity::class.java)
        startActivity(intent)
    }
}
