package com.iacademy.smartsoilph.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.FertilizerNutrientModel
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.datamodels.RecommendationData
import com.iacademy.smartsoilph.datamodels.RequiredFertilizerData
import com.iacademy.smartsoilph.models.FertilizerRecommendationModel
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.CheckInternet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SoilActivity : BaseActivity() {

    //declare layout variables
    private lateinit var actvCropType: AutoCompleteTextView
    private lateinit var etNitrogen: EditText
    private lateinit var etPhosphorus: EditText
    private lateinit var etPotassium: EditText
    private lateinit var etPHLevel: EditText
    private lateinit var etECLevel: EditText
    private lateinit var etHumidity: EditText
    private lateinit var etTemperature: EditText
    private lateinit var btnFilter: ImageView
    private lateinit var btnViewRecommendation: CardView
    private lateinit var btnRetrieveData: CardView
    private lateinit var btnReturn: ImageView
    private var gradeDialog: Dialog? = null

    //floating action button
    private lateinit var fabViewRecommend: FloatingActionButton
    private lateinit var fabRetrieveData: FloatingActionButton
    private lateinit var fabSettings: FloatingActionButton
    private var rotate = false

    //declare dialog variable
    private lateinit var tvSoilTexture: TextView
    private var isSoilTextureSelected = false

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onResume() {
        super.onResume()

        //dropdown
        val crops = resources.getStringArray(R.array.crops)
        // Specify the custom layout and the ID of the TextView within that layout
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, R.id.tv_1, crops)
        actvCropType = findViewById<AutoCompleteTextView>(R.id.actv_crop_type)
        actvCropType.setAdapter(arrayAdapter)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize layout and button navigations
        initializeLayout()
        setupButtonNavigation()
    }

    //show overlay
    private fun initShowOut(v: View){
        v.apply {
            visibility = View.GONE
            translationY = height.toFloat()
            alpha = 0f
        }
    }

    //shows other fabs
    private fun toggleFabMode(v: View) {
        rotate = rotateFab(v,!rotate)
        if (rotate){
            showIn(fabViewRecommend)
            showIn(fabRetrieveData)
            showIn(btnViewRecommendation)
            showIn(btnRetrieveData)
        }else{
            showOut(fabViewRecommend)
            showOut(fabRetrieveData)
            showOut(btnViewRecommendation)
            showOut(btnRetrieveData)
        }
    }

    //animates settings fab when clicked
    private fun showOut(view: View) {
        view.apply {
            visibility = View.VISIBLE
            alpha = 1f
            translationY = 0f
            animate()
                .setDuration(200)
                .translationY(height.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                        super.onAnimationEnd(animation)
                    }
                })
                .alpha(0f)
                .start()
        }
    }

    private fun showIn(view: View) {
        view.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = height.toFloat()
            animate()
                .setDuration(200)
                .translationY(0f)
                .setListener(object : AnimatorListenerAdapter() {})
                .alpha(1f)
                .start()
        }
    }

    //rotation animation for setitngs fab
    private fun rotateFab(v: View, rotate: Boolean): Boolean {
        v.animate()
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(if (rotate) 180f else 0f)
        return rotate
    }

    private fun initializeLayout() {
        etNitrogen = findViewById<EditText>(R.id.nitrogen_value);
        etPhosphorus = findViewById<EditText>(R.id.phosphorus_value);
        etPotassium = findViewById<EditText>(R.id.potassium_value);
        etPHLevel = findViewById<EditText>(R.id.ph_level_value);
        etECLevel = findViewById<EditText>(R.id.ec_level_value);
        etHumidity = findViewById<EditText>(R.id.humidity_soil_value);
        etTemperature = findViewById<EditText>(R.id.temp_soil_value);
        btnFilter = findViewById<ImageView>(R.id.btn_filter);
        btnViewRecommendation = findViewById<CardView>(R.id.btn_recommend);
        btnRetrieveData = findViewById<CardView>(R.id.btn_retrieveData);
        btnReturn = findViewById<ImageView>(R.id.toolbar_back_icon)
        tvSoilTexture = findViewById<TextView>(R.id.tv_soil_texture)

        fabViewRecommend = findViewById<FloatingActionButton>(R.id.fab_recommendation)
        fabRetrieveData = findViewById<FloatingActionButton>(R.id.fab_retrieveData)
        fabSettings = findViewById<FloatingActionButton>(R.id.fab_settings)

    }

    private fun setupButtonNavigation() {
        // Button Logistics
        btnFilter.setOnClickListener {
            Log.d("STATE", "FILTER CLICKEEEEEEEEEEEEEEEEEEEEEEEEEEEEEED")
            showGradeDialog()
        }

        //fab view recommendation
        fabViewRecommend.setOnClickListener {
            Log.d("STATE", "VIEW RECOMMENDATION CLICKEEEEEEEEEEEEEEEEEEEEEEEEEEEEEED ")
            isSoilTextureSelected = true
            if (isSoilTextureSelected) {
                recommendation()
            } else {
                Toast.makeText(this, "Please select a soil texture first", Toast.LENGTH_SHORT).show()
            }
        }

        //fab setting
        val overlayView = findViewById<View>(R.id.overlay_bg)
        fabSettings.setOnClickListener {
            // If the overlay is visible, hide it, otherwise show it
            if (overlayView.visibility == View.VISIBLE) {
                overlayView.visibility = View.GONE
            } else {
                overlayView.visibility = View.VISIBLE
            }
            toggleFabMode(it)
        }

        overlayView.setOnClickListener {
            overlayView.visibility = View.GONE
            if (overlayView.visibility == View.GONE){
                rotate = rotateFab(fabSettings, false) // Rotate the fab back to its original position
                showOut(fabViewRecommend)
                showOut(fabRetrieveData)
                showOut(btnViewRecommendation)
                showOut(btnRetrieveData)
            }else{
                showIn(fabViewRecommend)
                showIn(fabRetrieveData)
                showIn(btnViewRecommendation)
                showIn(btnRetrieveData)
            }
        }


        btnReturn.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        //dropdown
        val crops = resources.getStringArray(R.array.crops)
        // Specify the custom layout and the ID of the TextView within that layout
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, R.id.tv_1, crops)
        actvCropType = findViewById<AutoCompleteTextView>(R.id.actv_crop_type)
        actvCropType.setAdapter(arrayAdapter)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    /*****************************
     * A. Show Soil Texture
     ***************************/
    private fun showGradeDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_choose_grade)

        // Initialize RadioButtons using Loop
        val radioButtons = mutableListOf<RadioButton>()
        for (i in 1..9) {
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

        // Auto select "Clay" or "gs_9"
        val clayRadioButton = dialog.findViewById<RadioButton>(R.id.gs_9)
        clayRadioButton.isChecked = true
        selectedRadioButton = clayRadioButton

        // Initialize Apply button
        val btnApply = dialog.findViewById<CardView>(R.id.btn_apply)
        btnApply.setOnClickListener {
            val selectedRadioButton = radioButtons.find { it.isChecked }
            if (selectedRadioButton != null) {
                // Mark true that a grade has been selected
                isSoilTextureSelected = true
                val selectedTexture = selectedRadioButton.text.toString()
                tvSoilTexture.text = "$selectedTexture"
                dialog.dismiss()
            } else {
                Toast.makeText(this, R.string.dialog_soil_texture1, Toast.LENGTH_SHORT).show()
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
        Log.d("STATE", "1 INSIDE")
        // Get values from EditText fields
        val cropType = actvCropType.text.toString()
        val nitrogen = etNitrogen.text.toString().toFloatOrNull() ?: 0.0F
        val phosphorus = etPhosphorus.text.toString().toFloatOrNull() ?: 0.0F
        val potassium = etPotassium.text.toString().toFloatOrNull() ?: 0.0F
        val phLevel = etPHLevel.text.toString().toFloatOrNull() ?: 0.0F
        val ecLevel = etECLevel.text.toString().toFloatOrNull() ?: 0.0F
        val humidity = etHumidity.text.toString().toFloatOrNull() ?: 0.0F
        val temperature = etTemperature.text.toString().toFloatOrNull() ?: 0.0F
        val soilTexture = tvSoilTexture.text.toString()

        /******************************
         * Computations
         * ---------------------------*/
        // Retrieve selected crop and detected NPK values from the intent
        val selectedCrop = cropType
        // Fetch the nutrient requirements for the selected crop
        val fertilizerNutrientModel = FertilizerNutrientModel()
        val requirements = fertilizerNutrientModel.getNutrientRequirements(selectedCrop)

        // Compute required fertilizers
        requirements?.let {
            // Calculate Crop Nutrient Requirement
            val (requiredN, labelN) = calculateRequiredFertilizer(nitrogen, it.nitrogenRequirements)
            val (requiredP, labelP) = calculateRequiredFertilizer(phosphorus, it.phosphorusRequirements)
            val (requiredK, labelK) = calculateRequiredFertilizer(potassium, it.potassiumRequirements)

            // Calculate Amount of Fertilizer Recommendation
            val calculator = FertilizerRecommendationModel()
            val data  = calculator.calculateFertilizerRequirements(
                requiredN.toFloat(),requiredP.toFloat(),requiredK.toFloat(),nitrogen
            )

            /******************************
             * Pass values to Soil DataModel
             * ---------------------------*/
            val soilData = SoilData(cropType, nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature, soilTexture)
            Log.d("", "5. SOIL DATA: $soilData")

            /******************************************************
             * Pass computed values to RequiredFertilizer DataModel
             * --------------------------------------------------*/
            val requiredFertilizerData = RequiredFertilizerData(
                requiredN.toFloat(), requiredP.toFloat(), requiredK.toFloat(),
                data.fertilizer1, data.fertilizer2, data.fertilizer3,
                data.kgFertilizer1, data.kgFertilizer2, data.kgFertilizer3,
                data.bagFertilizer1, data.bagFertilizer2, data.bagFertilizer3
            )

            /******************************
             * Pass values to Datamodel
             * ---------------------------*/
            //Create SQLite instance
            val dbHelper = SQLiteModel(this)
            // Get the current user's UID
            val currentUserUID = dbHelper.getCurrentUserUID() ?: return // Add appropriate error handling or fallback
            // Generate a unique recommendationID
            val recommendationID = dbHelper.generateRecommendationID()

            // Get Date
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.getDefault())
            val dateOfRecommendation = formatter.format(calendar.time)

            /********************************************
             * Pass values to Recommendation DataModel
             * ------------------------------------------*/
            Log.d("", "11. Passing to Recommendation DataModel")
            val storageType = "Local SQLite"
            val isSavedOnline = false
            val recommendationData = RecommendationData(recommendationID, currentUserUID, soilData, requiredFertilizerData, dateOfRecommendation, storageType, isSavedOnline)

            Log.d("", "12. RECOMMENDATION DATA")
            Log.d("", "recommendationID: $recommendationID")
            Log.d("", "currentUserUID: $currentUserUID")
            Log.d("", "cropType: $cropType")
            Log.d("", "soilData: $soilData")
            Log.d("", "soilTexture: $soilTexture")
            Log.d("", "requiredFertilizerData: $requiredFertilizerData")
            Log.d("", "dateOfRecommendation: $dateOfRecommendation")
            Log.d("", "storageType: $storageType")
            Log.d("", "savedOnline: $isSavedOnline")

            /******************************
             * Check Internet Connectivity
             * ---------------------------*/
            val checkInternet = CheckInternet(this)
            if (checkInternet.isInternetAvailable()) {
                // Internet is available - save locally then add to Firebase Database
                // Save locally first
                recommendationData.initialStorageType = "Cloud Firebase"
                recommendationData.isSavedOnline = true
                dbHelper.addSoilData(recommendationData)
                // Save in cloud
                FirebaseModel().saveSoilData(soilData, auth)
                FirebaseModel().saveRecommendation(recommendationData, auth)
                //Sync Database
                dbHelper.syncDataWithFirebase(auth)
                Toast.makeText(this, R.string.dialog_sync_database_result, Toast.LENGTH_SHORT).show()
            } else {
                // Internet is NOT available - add to SQLite
                dbHelper.addSoilData(recommendationData)  // Save the soil data to the SQLite database
            }

            /***************************
             * Go to FertilizerActivity
             * ---------------------------*/
            val intent = Intent(this, FertilizerActivity::class.java).apply {
                putExtra("labelN", labelN)
                putExtra("labelP", labelP)
                putExtra("labelK", labelK)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

    }

    private fun calculateRequiredFertilizer(detectedValue: Float, requirements: Map<ClosedFloatingPointRange<Float>, Pair<Int, String>>): Pair<Int, String> {
        // Find the range that the detected value falls into
        val requirementEntry = requirements.entries.find { detectedValue in it.key }
        // Return the corresponding fertilizer amount and label
        return requirementEntry?.value ?: Pair(0, "Unknown")
    }
}
