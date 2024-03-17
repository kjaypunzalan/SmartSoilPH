package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.models.FertilizerNutrientModel

class FertilizerActivity : BaseActivity() {

    //declare layout variables
    private lateinit var tvUserName: TextView
    private lateinit var tvFertilizerRecommendation: TextView
    private lateinit var tvNitrogen: TextView
    private lateinit var tvPhosphorus: TextView
    private lateinit var tvPotassium: TextView
    private lateinit var tvNLabel: TextView
    private lateinit var tvPLabel: TextView
    private lateinit var tvKLabel: TextView
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
        tvNLabel = findViewById<TextView>(R.id.npk_rating1)
        tvPLabel = findViewById<TextView>(R.id.npk_rating2)
        tvKLabel = findViewById<TextView>(R.id.npk_rating3)
        tvPHLevel = findViewById<TextView>(R.id.tv_ph_amount)
        tvPHLevelLabel = findViewById<TextView>(R.id.ph_value)
        tvPHLevelDescription = findViewById<TextView>(R.id.ph_recommend)
        btnPreviousRecommendations = findViewById<CardView>(R.id.btn_previous);
        btnReturnSoil = findViewById<CardView>(R.id.btn_return_soil);
    }

    private fun setupButtonNavigation() {
        btnReturnSoil.setOnClickListener {
            val Intent = Intent(this, SoilActivity::class.java);
            intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(Intent);
            finish()
        }
        btnPreviousRecommendations.setOnClickListener {
            val Intent = Intent(this, RecommendationHistoryActivity::class.java);
            intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(Intent);
            finish()
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

    /***********************************
     * B. Fetch content from SQLite
     *---------------------------------*/
    private fun fetchFromSQLite() {
        val dbHelper = SQLiteModel(this)
        val latestSoilData = dbHelper.getLatestSoilData()

        if (latestSoilData != null) {
            // Display Fertilizer Requirement
            val soil = latestSoilData.soilData
            val fertilizer = latestSoilData.requiredFertilizerData

            // Display Required NPK
            val n = String.format("%.0f", fertilizer.requiredN)
            val p = String.format("%.0f", fertilizer.requiredP)
            val k = String.format("%.0f", fertilizer.requiredK)
            tvNitrogen.text = n
            tvPhosphorus.text = p
            tvPotassium.text = k

            // Display Label
            tvNLabel.text = intent.getStringExtra("labelN") ?: ""
            tvPLabel.text = intent.getStringExtra("labelP") ?: ""
            tvKLabel.text = intent.getStringExtra("labelK") ?: ""

            // Display Fertilizer Recommendation
            val fertilizer1String = "${fertilizerString(fertilizer.kgFertilizer1, fertilizer.fertilizer1, fertilizer.bagFertilizer1)}\n"
            val fertilizer2String =
                if (fertilizer.fertilizer2.isNotEmpty()) "${fertilizerString(fertilizer.kgFertilizer2, fertilizer.fertilizer2, fertilizer.bagFertilizer2)}\n"
                else ""
            val fertilizer3String =
                if (fertilizer.fertilizer3.isNotEmpty()) "${fertilizerString(fertilizer.kgFertilizer3, fertilizer.fertilizer3, fertilizer.bagFertilizer3)}"
                else ""
            val fertilizerString = fertilizer1String + fertilizer2String + fertilizer3String
            tvFertilizerRecommendation.text = fertilizerString

            // Display pH Level
            val phLevelValue = latestSoilData.soilData.phLevel
            val phLevelString = String.format("%.1f", phLevelValue)
            tvPHLevel.text = phLevelString

            // Display pH Level Label
            val phLevelLabelText: String = when {
                phLevelValue < 5.5 -> getString(com.iacademy.smartsoilph.R.string.ph_sentence_value1)
                phLevelValue > 6.5 -> getString(com.iacademy.smartsoilph.R.string.ph_sentence_value3)
                else -> getString(com.iacademy.smartsoilph.R.string.ph_sentence_value2)
            }
            tvPHLevelLabel.text = phLevelLabelText

            // Display pH Level Description
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

    private fun fertilizerString(fertilizerAmount: Float, fertilizerName: String, fertilizerBag: Float) : String {
        val convertedFertilizerAmount = String.format("%.2f", fertilizerAmount)
        val convertedFertilizerBag = String.format("%.1f", fertilizerBag)
        return when (fertilizerName) {
            "Complete" -> { "$convertedFertilizerAmount kg of $fertilizerName (14-14-14). ($convertedFertilizerBag bag)" }
            "Ammonium Phosphate" -> { "$convertedFertilizerAmount kg of $fertilizerName (16-20-14) ($convertedFertilizerBag bag)" }
            "Ammonium Sulfate" -> { "$convertedFertilizerAmount kg of $fertilizerName (21-0-0) ($convertedFertilizerBag bag)" }
            "Urea" -> { "$convertedFertilizerAmount kg of $fertilizerName (46-0-0) ($convertedFertilizerBag bag)" }
            "Duophos" -> { "$convertedFertilizerAmount kg of $fertilizerName (0-22-0) ($convertedFertilizerBag bag)" }
            "Muriate of Potash" -> { "$convertedFertilizerAmount kg of $fertilizerName (0-0-60) ($convertedFertilizerBag bag)" }
            else -> ""
        }
    }


}