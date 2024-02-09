package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.appcompat.app.AlertDialog


class HomeActivity : BaseActivity() {

    //declare layout variables
    private lateinit var btnSoil: CardView
    private lateinit var btnWeather: CardView
    private lateinit var btnReports: CardView
    private lateinit var btnManual: CardView
    private lateinit var btnLogout: CardView
    private lateinit var tvUsername: TextView
    private lateinit var tvDateToday: TextView
    private lateinit var btnSyncDatabase: ImageView
    private lateinit var btnLanguage: ImageView

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        initializeLayout()
        setupButtonNavigation()

        //Display Date and Username
        displayCurrentDate()
        fetchUsername()

        //resetDatabase()
    }

    private fun initializeLayout() {
        btnSoil = findViewById<CardView>(R.id.soil_card)
        btnWeather = findViewById<CardView>(R.id.weather_card)
        btnReports = findViewById<CardView>(R.id.reports_card)
        btnManual = findViewById<CardView>(R.id.manual_card)
        btnLogout = findViewById<CardView>(R.id.logout_card)
        tvUsername = findViewById<TextView>(R.id.tv_username)
        tvDateToday = findViewById<TextView>(R.id.tv_date_today)
        btnSyncDatabase = findViewById<ImageView>(R.id.btn_sync_database)
        btnLanguage = findViewById<ImageView>(R.id.btn_language)
    }

    private fun setupButtonNavigation() {
        // set click listeners for buttons
        setButtonClickListener(btnSoil, SoilActivity::class.java)
        setButtonClickListener(btnWeather, WeatherActivity::class.java)
        setButtonClickListener(btnReports, ReportsActivity::class.java)
        setButtonClickListener(btnManual, RecommendationHistoryActivity::class.java)

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSyncDatabase.setOnClickListener {
            showSyncDatabaseDialog()
        }

        btnLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    //Button Function
    private fun setButtonClickListener(button: CardView, activityClass: Class<*>) {
        button.setOnClickListener {
            val intent = Intent(this, activityClass)
            startActivity(intent)
        }
    }

    private fun displayCurrentDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val currentDate = dateFormat.format(Calendar.getInstance().time)
        tvDateToday.text = currentDate
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

    private fun showSyncDatabaseDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to sync database? Make sure you are connected to the internet.")
            .setCancelable(false)
            .setPositiveButton("Sync Database") { dialog, id ->
                // Perform the database sync operation
                val dbHelper = DatabaseHelper(this)
                dbHelper.syncDataWithFirebase(auth, this)
                Toast.makeText(this, "Database syncing...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, id ->
                // Close the dialog
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Database Sync")
        alert.show()
    }

    private fun showLanguageDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val languages = arrayOf("English", "Tagalog")
        var chosenLanguage = 0 // 0 for English, 1 for Tagalog

        dialogBuilder.setSingleChoiceItems(languages, chosenLanguage) { dialog, which ->
            chosenLanguage = which
        }

        dialogBuilder.setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                if (chosenLanguage == 1) {
                    setLocale("fil") // Tagalog
                    recreate() // Recreate the activity to apply the language change
                } else {
                    setLocale("en") // English or default language
                    recreate()
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Set SmartSoilPH Language")
        alert.show()
    }

    private fun setLocale(languageCode: String) {
        // Save the chosen language in SharedPreferences or any persistent storage
        // so you can load it on app start next time.
        val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
    }

    private fun resetDatabase() {
        val dbHelper = DatabaseHelper(this)
        dbHelper.deleteDatabase()
        // Show a Toast message confirming the database reset
        Toast.makeText(this, "Database has been reset", Toast.LENGTH_SHORT).show()
    }
}