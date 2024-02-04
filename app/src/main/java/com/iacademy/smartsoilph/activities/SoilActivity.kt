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
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.datamodels.RecommendationData
import com.iacademy.smartsoilph.models.DatabaseHelper
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.CheckInternet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    private lateinit var btnViewRecommendation: CardView

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
        initializeLayout()
        initializeButtons()
    }

    private fun initializeLayout() {
        etNitrogen = findViewById<EditText>(R.id.nitrogen_value);
        etPhosphorus = findViewById<EditText>(R.id.phosphorus_value);
        etPotassium = findViewById<EditText>(R.id.potassium_value);
        etPHLevel = findViewById<EditText>(R.id.ph_level_value);
        etECLevel = findViewById<EditText>(R.id.ec_level_value);
        etHumidity = findViewById<EditText>(R.id.humidity_soil_value);
        etTemperature = findViewById<EditText>(R.id.temp_soil_value);
        btnFilter = findViewById<CardView>(R.id.btn_filter);
        btnViewRecommendation = findViewById<CardView>(R.id.btn_view_fertilizer);
        selectedGradeTextView = findViewById<TextView>(R.id.tv_selected_grade)
    }

    private fun initializeButtons() {
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

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    /*****************************
     * A. Show Grade Dialog
     ***************************/
    private fun showGradeDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_choose_grade)

        // Initialize RadioButtons using Loop
        val radioButtons = mutableListOf<RadioButton>()
        for (i in 1..12) {
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
        val nitrogen = etNitrogen.text.toString().toFloatOrNull() ?: 0.0F
        val phosphorus = etPhosphorus.text.toString().toFloatOrNull() ?: 0.0F
        val potassium = etPotassium.text.toString().toFloatOrNull() ?: 0.0F
        val phLevel = etPHLevel.text.toString().toFloatOrNull() ?: 0.0F
        val ecLevel = etECLevel.text.toString().toFloatOrNull() ?: 0.0F
        val humidity = etHumidity.text.toString().toFloatOrNull() ?: 0.0F
        val temperature = etTemperature.text.toString().toFloatOrNull() ?: 0.0F

        /******************************
         * Computations
         * ---------------------------*/
        /* SOIL NPK COMPUTATIONS */
        // Calculate fertilizer application rate
        val desiredNApplicationRate = 43.0F // Example N application rate in lbs/acre
        val fertilizerApplicationRate =
            desiredNApplicationRate / ((nitrogen / 100.0F).takeIf { it > 0 } ?: 1.0F)

        // Calculate fertilizer weight required for 1 acre
        val lawnArea = 1.0F // Example lawn area in acres
        val fertilizerWeightRequired = fertilizerApplicationRate * lawnArea
        val fertilizerWeightKg = fertilizerWeightRequired * 0.453592F // Convert weight to kilograms

        /* LIME COMPUTATIONS */
        // Calculate lime requirement
        val targetPH = 5.5F // Example target pH
        val currentPH = phLevel
        val soilTextureFactor =
            when {
                currentPH >= 5.5F -> 0.0F // No lime needed if current pH is already above target
                phLevel <= 7.0F -> 4.0F // Loam to clay loam
                phLevel <= 8.0F -> 3.0F // Sandy loam
                else -> 2.0F // Sand
            }
        val limeRequirement = (targetPH - currentPH) * soilTextureFactor


        /******************************
         * Pass values to Datamodel
         * ---------------------------*/
        //get Date
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.getDefault())
        val dateOfRecommendation = formatter.format(calendar.time)
        //pass values to Datamodel
        val soilData = SoilData(nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature)
        val recommendationData = RecommendationData(soilData, fertilizerWeightKg, limeRequirement, dateOfRecommendation, "Cloud Firebase")


        /******************************
         * Check Internet Connectivity
         * ---------------------------*/
        val checkInternet = CheckInternet(this)
        if (checkInternet.isInternetAvailable()) {
            // Internet is available - add to Firebase Database
            FirebaseModel().saveSoilData(soilData, auth)
            FirebaseModel().saveRecommendation(recommendationData, auth)
        } else {
            // Internet is NOT available - add to SQLite
            recommendationData.initialStorageType = "Local SQLite"
            val dbHelper = DatabaseHelper(this)             // Instantiate DatabaseHelper
            val result = dbHelper.addSoilData(recommendationData)  // Save the soil data to the SQLite database

            //Add toast notification is successful or not
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
