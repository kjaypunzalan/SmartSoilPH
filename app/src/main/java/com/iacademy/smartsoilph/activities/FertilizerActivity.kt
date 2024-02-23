package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
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
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.CheckInternet
import com.iacademy.smartsoilph.datamodels.FertilizerNutrientModel
import com.iacademy.smartsoilph.models.FertilizerCalculatorModel

class FertilizerActivity : BaseActivity() {

    //declare layout variables
    private lateinit var tvUserName: TextView
    private lateinit var tvFertilizerRecommendation: TextView
    private lateinit var tvNitrogen: TextView
    private lateinit var tvPhosphorus: TextView
    private lateinit var tvPotassium: TextView
    private lateinit var tvPHLevel: TextView
    private lateinit var tvPHLevelLabel: TextView
    private lateinit var tvPHLevelDescription: TextView
    private lateinit var btnPreviousRecommendations: CardView
    private lateinit var btnReturnSoil: CardView

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    //declare and initialize the model
    private val fertilizerNutrientModel = FertilizerNutrientModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fertilizer)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        initializeLayout()

        //Buttons
        setupButtonNavigation()

        //Initialize Content
        initializeContent()
    }

    private fun initializeLayout() {
        tvFertilizerRecommendation = findViewById<TextView>(R.id.complete_value)
        tvNitrogen = findViewById<TextView>(R.id.npk_value1)
        tvPhosphorus = findViewById<TextView>(R.id.npk_value2)
        tvPotassium = findViewById<TextView>(R.id.npk_value3)
        tvPHLevel = findViewById<TextView>(R.id.tv_ph_amount)
        tvPHLevelLabel = findViewById<TextView>(R.id.ph_value)
        tvPHLevelDescription = findViewById<TextView>(R.id.ph_recommend)
        btnPreviousRecommendations = findViewById<CardView>(R.id.btn_previous);
        btnReturnSoil = findViewById<CardView>(R.id.btn_return_soil);
    }

    private fun setupButtonNavigation() {
        btnReturnSoil.setOnClickListener {
            val Intent = Intent(this, SoilActivity::class.java);
            startActivity(Intent);
        }
        btnPreviousRecommendations.setOnClickListener {
            val Intent = Intent(this, RecommendationHistoryActivity::class.java);
            startActivity(Intent);
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, SoilActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    /******************************************************
     * A. Initialize Content from Firebase or from SQLite
     *----------------------------------------------------*/
    private fun initializeContent() {
        fetchFromSQLite()
    }

    private fun displayNutrientRequirements(nitrogen: Float, phosphorus: Float, potassium: Float) {
        // Retrieve selected crop and detected NPK values from the intent
        val selectedCrop = "Eggplant" // Example crop

        // Fetch the nutrient requirements for the selected crop
        val requirements = fertilizerNutrientModel.getNutrientRequirements(selectedCrop)

        // Compute required fertilizers and display them
        requirements?.let {
            val (requiredN, labelN) = calculateRequiredFertilizer(nitrogen, it.nitrogenRequirements)
            val (requiredP, labelP) = calculateRequiredFertilizer(phosphorus, it.phosphorusRequirements)
            val (requiredK, labelK) = calculateRequiredFertilizer(potassium, it.potassiumRequirements)

            tvNitrogen.text = "$requiredN"
            tvPhosphorus.text = "$requiredP"
            tvPotassium.text = "$requiredK"

            val calculator = FertilizerCalculatorModel()
            val fertilizerRequirements = calculator.calculateFertilizerRequirements(
                nRequirement = requiredN.toFloat(),
                pRequirement = requiredP.toFloat(),
                kRequirement = requiredK.toFloat(),
                initialN = nitrogen
            )

            // Convert to a readable string format to display
            val recommendationStr = fertilizerRequirements.entries.joinToString(separator = "\n") {
                "${it.key}: ${String.format("%.2f kg", it.value)}"
            }

            // Display in TextView
            tvFertilizerRecommendation.text = recommendationStr
        }
    }

    private fun calculateRequiredFertilizer(detectedValue: Float, requirements: Map<ClosedFloatingPointRange<Float>, Pair<Int, String>>): Pair<Int, String> {
        // Find the range that the detected value falls into
        val requirementEntry = requirements.entries.find { detectedValue in it.key }
        // Return the corresponding fertilizer amount and label
        return requirementEntry?.value ?: Pair(0, "Unknown")
    }




    /***********************************
     * B. Fetch content from SQLite
     *---------------------------------*/
    private fun fetchFromSQLite() {
        val dbHelper = SQLiteModel(this)
        val latestSoilData = dbHelper.getLatestSoilData()

        if (latestSoilData != null) {
            //Display Fertilizer Requirement
            val soil = latestSoilData.soilData
            displayNutrientRequirements(soil.nitrogen, soil.phosphorus, soil.potassium)

            //Display pH Level
            val phLevelValue = latestSoilData.soilData.phLevel
            val phLevelString = String.format("%.1f", phLevelValue)
            tvPHLevel.text = phLevelString

            //Display pH Level Label
            val phLevelLabelText: String = when {
                phLevelValue < 5.5 -> getString(com.iacademy.smartsoilph.R.string.ph_sentence_value1)
                phLevelValue > 6.5 -> getString(com.iacademy.smartsoilph.R.string.ph_sentence_value3)
                else -> getString(com.iacademy.smartsoilph.R.string.ph_sentence_value2)
            }
            tvPHLevelLabel.text = phLevelLabelText

            //Display pH Level Description
            val phLevelDescriptionText: String = when {
                phLevelValue < 5.5 -> getString(com.iacademy.smartsoilph.R.string.ph_info1)
                phLevelValue > 6.5 -> getString(com.iacademy.smartsoilph.R.string.ph_info3)
                else -> getString(com.iacademy.smartsoilph.R.string.ph_info2)
            }
            tvPHLevelDescription.text = phLevelDescriptionText


        } else {
            Toast.makeText(applicationContext, "No local soil data available", Toast.LENGTH_SHORT).show()
        }
    }

}