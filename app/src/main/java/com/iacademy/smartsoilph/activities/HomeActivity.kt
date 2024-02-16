package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.models.FirebaseModel
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.iacademy.smartsoilph.arduino.BluetoothController


class HomeActivity : BaseActivity() {

    // Declare layout variables
    private lateinit var btnSoil: CardView
    private lateinit var btnWeather: CardView
    private lateinit var btnReports: CardView
    private lateinit var btnManual: CardView
    private lateinit var btnLogout: CardView
    private lateinit var tvUsername: TextView
    private lateinit var tvDateToday: TextView
    private lateinit var btnBtConnect: CardView
    private lateinit var btnSettings: ImageView

    // Declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize variables
        initializeLayout()
        setupButtonNavigation()

        // Display Date and Username
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

        //settings
        btnSettings = findViewById<ImageView>(R.id.btn_settings)
        btnSoil = findViewById(R.id.soil_card)
        btnWeather = findViewById(R.id.weather_card)
        btnReports = findViewById(R.id.reports_card)
        btnManual = findViewById(R.id.manual_card)
        btnLogout = findViewById(R.id.logout_card)
        tvUsername = findViewById(R.id.tv_username)
        tvDateToday = findViewById(R.id.tv_date_today)
        btnBtConnect = findViewById(R.id.bluetooth_card)
        // Note: btnSyncDatabase and other menu items are handled through onCreateOptionsMenu and onOptionsItemSelected
    }

    private fun setupButtonNavigation() {
        // set click listeners for buttons
        setButtonClickListener(btnSoil, SoilActivity::class.java)
        setButtonClickListener(btnWeather, WeatherActivity::class.java)
        setButtonClickListener(btnReports, ReportsActivity::class.java)
        setButtonClickListener(btnManual, RecommendationHistoryActivity::class.java)
        setButtonClickListener(btnBtConnect, ArduinoController::class.java) // Ensure BluetoothController exists or adjust as needed

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        //settings
        btnSettings.setOnClickListener { view ->
            showPopupMenu(view)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.popup_menu, menu) // Replace "your_menu_xml_file_name" with the actual file name of your menu
        return true
    }

    private fun showPopupMenu(view: View?) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        try {
            val popupField = PopupMenu::class.java.getDeclaredField("mPopup")
            popupField.isAccessible = true
            val menuPopupHelper = popupField.get(popup)
            val setForceShowIconMethod: Method = menuPopupHelper.javaClass.getDeclaredMethod(
                "setForceShowIcon", Boolean::class.javaPrimitiveType
            )
            setForceShowIconMethod.invoke(menuPopupHelper, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btn_sync_database -> {
                    // Handle sync action
                    showSyncDatabaseDialog()
                    true
                }
                R.id.btn_language -> {
                    // Handle change language action
                    showLanguageDialog()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_sync_database -> {
                showSyncDatabaseDialog()
                true
            }
            R.id.btn_language -> {
                showLanguageDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        val dbHelper = SQLiteModel(this)
        val username = dbHelper.getCurrentUserName()
        // Check if username is not null
        if (username != null) {
            // Set the username to the TextView
            tvUsername.text = " $username"
        } else {
            // Handle case where username is null, maybe set a default name or prompt user
            tvUsername.text = " User"
        }
    }

    private fun showSyncDatabaseDialog() {
        Log.d("", "------------------------1. Dialog Showed Up")
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to sync database? Make sure you are connected to the internet.")
            .setCancelable(false)
            .setPositiveButton("Sync Database") { dialog, id ->
                // Perform the database sync operation
                val dbHelper = SQLiteModel(this)
                Log.d("", "------------------------2. Dialog Sync Clicked")
                dbHelper.syncDataWithFirebase(auth)
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
        val dbHelper = SQLiteModel(this)
        dbHelper.deleteDatabase()
        // Show a Toast message confirming the database reset
        Toast.makeText(this, "Database has been reset", Toast.LENGTH_SHORT).show()
    }
}