package com.iacademy.smartsoilph.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.arduino.BluetoothController
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.datamodels.RecommendationData
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.CheckInternet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SoilActivityTest : BaseActivity() {

    //declare layout variables
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
    private var rotate = false

    //declare dialog variable
    private lateinit var selectedGradeTextView: TextView
    private var isGradeSelected = false

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    //declare btcontroller
    private lateinit var bluetoothController: BluetoothController

    //broadcast receiver btcontroller
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.iacademy.smartsoilph.arduino.ACTION_UPDATE_DATA") {
                val val1 = intent.getStringExtra("val1") ?: ""
                val val2 = intent.getStringExtra("val2") ?: ""
                val val3 = intent.getStringExtra("val3") ?: ""
                val val4 = intent.getStringExtra("val4") ?: ""
                val val5 = intent.getStringExtra("val5") ?: ""
                val val6 = intent.getStringExtra("val6") ?: ""
                val val7 = intent.getStringExtra("val7") ?: ""

                etNitrogen.setText(val1)
                etPhosphorus.setText(val2)
                etPotassium.setText(val3)
                etPHLevel.setText(val4)
                etECLevel.setText(val5)
                etHumidity.setText(val6)
                etTemperature.setText(val7)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, IntentFilter("com.iacademy.smartsoilph.arduino.ACTION_UPDATE_DATA"))

        //dropdown
        val crops = resources.getStringArray(R.array.crops)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, R.id.tv_1, crops)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoComplete)
        autoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize layout and button navigations
        initializeLayout()
        setupButtonNavigation()
        Log.d("STATE", "OMGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG")

        //dropdown
        val crops = resources.getStringArray(R.array.crops)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, crops)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoComplete)
        autoCompleteTextView.setAdapter(arrayAdapter)

        bluetoothController = BluetoothController(this).apply {
            setDataListener(object : BluetoothController.BluetoothDataListener {
                override fun onDataReceived(data: String) {
                    runOnUiThread {
                    }
                }
            })
        }

        if (!bluetoothController.connect()) {
            // Show a message if failed to connect
        }

        //fab setting
        val overlayView = findViewById<View>(R.id.overlay_bg)
        val settingFab = findViewById<FloatingActionButton>(R.id.fab_settings)
        settingFab.setOnClickListener {
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
        }

        //sensor retrieve button
        val fabRetrieveData = findViewById<FloatingActionButton>(R.id.fab_retrieveData)
        fabRetrieveData.setOnClickListener {
            bluetoothController.sendCommand("1")
        }
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
        selectedGradeTextView = findViewById<TextView>(R.id.tv_selected_grade)

        fabViewRecommend = findViewById<FloatingActionButton>(R.id.fab_recommendation);

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
            if (isGradeSelected) {
                recommendation()
            } else {
                Toast.makeText(this, "Please select a grade first", Toast.LENGTH_SHORT).show()
            }
        }

        //cardview view recommendation
        btnViewRecommendation.setOnClickListener {
            Log.d("STATE", "VIEW RECOMMENDATION CLICKEEEEEEEEEEEEEEEEEEEEEEEEEEEEEED ")
            if (isGradeSelected) {
                recommendation()
            } else {
                Toast.makeText(this, "Please select a grade first", Toast.LENGTH_SHORT).show()
            }
        }

        //cardview retrieve data
        btnRetrieveData.setOnClickListener {
            bluetoothController.sendCommand("1")
        }

        btnReturn.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("MissingSuperCall")
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
        //Create SQLite instance
        val dbHelper = SQLiteModel(this)
        // Get the current user's UID
        val currentUserUID = dbHelper.getCurrentUserUID() ?: return // Add appropriate error handling or fallback
        // Generate a unique recommendationID
        val recommendationID = dbHelper.generateRecommendationID()

        // Get Date
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.getDefault())
        val dateOfRecommendation = formatter.format(calendar.time)

        // Pass values to Datamodel
        val soilData = SoilData(nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature)
        val storageType = "Local SQLite"
        val isSavedOnline = false
        val recommendationData = RecommendationData(recommendationID, currentUserUID, soilData, fertilizerWeightKg, limeRequirement, dateOfRecommendation, storageType, isSavedOnline)
        Log.d("", "recommendationID: $recommendationID")
        Log.d("", "currentUserUID: $currentUserUID")
        Log.d("", "soilData: $soilData")
        Log.d("", "fertilizer: $fertilizerWeightKg")
        Log.d("", "lime: $limeRequirement")
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
        } else {
            // Internet is NOT available - add to SQLite
            dbHelper.addSoilData(recommendationData)  // Save the soil data to the SQLite database

        }

        /***************************
         * Go to FertilizerActivity
         * ---------------------------*/
        val intent = Intent(this, FertilizerActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothController.disconnect()
    }
}
