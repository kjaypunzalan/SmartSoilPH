package com.iacademy.smartsoilph.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.widget.NestedScrollView
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.utils.CheckInternet
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeActivity : BaseActivity() {

    // Declare layout variables
    private lateinit var btnSoil: CardView
    private lateinit var btnWeather: CardView
    private lateinit var btnReports: CardView
    private lateinit var btnManual: CardView
    private lateinit var btnBtConnect: CardView
    private lateinit var btnSettings: CardView
    private lateinit var btnRecommendationHistory: CardView

    private lateinit var tvUsername: TextView
    private lateinit var tvDateToday: TextView

    private lateinit var ivBGHome: ImageView
    private lateinit var btnSwitch: Switch
    private lateinit var nestedScrollView: NestedScrollView

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
    }

    /*********************************
     * A. Initializing Layout
     *-------------------------------*/
    private fun initializeLayout() {

        // Note: btnSyncDatabase and other menu items are handled through onCreateOptionsMenu and onOptionsItemSelected
        btnSettings = findViewById<CardView>(R.id.settings_card)
        btnSoil = findViewById(R.id.soil_card)
        btnWeather = findViewById(R.id.weather_card)
        btnReports = findViewById(R.id.reports_card)
        btnManual = findViewById(R.id.manual_card)
        tvUsername = findViewById(R.id.tv_username)
        tvDateToday = findViewById(R.id.tv_date_today)
        btnBtConnect = findViewById(R.id.bluetooth_card)
        btnRecommendationHistory = findViewById(R.id.fertilizer_card)
        btnSettings = findViewById(R.id.settings_card)
        btnSwitch = findViewById(R.id.switch_button)
        ivBGHome = findViewById(R.id.bg_home)
        nestedScrollView = findViewById(R.id.nested_scrollView)

        // Add Parallax Effect
        nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = nestedScrollView.scrollY // For ScrollView
            ivBGHome.translationY = scrollY * 0.5f // Adjust parallax effect speed here
        }
    }

    /*********************************
     * B. Display Date and Username
     *-------------------------------*/
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
            tvUsername.text = " Farmer"
        }
    }


    /*********************************
     * C. Button Logistics
     *-------------------------------*/
    private fun setButtonClickListener(button: CardView, activityClass: Class<*>) {
        button.setOnClickListener {
            val intent = Intent(this, LoadScreenActivity::class.java)
            intent.putExtra(LoadScreenActivity.EXTRA_TARGET_ACTIVITY, activityClass.name) // loads loading screen before targetActivity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupButtonNavigation() {
        // set click listeners for buttons
        setButtonClickListener(btnSoil, SoilActivity::class.java)
        setButtonClickListener(btnReports, ReportsActivity::class.java)
        setButtonClickListener(btnRecommendationHistory, RecommendationHistoryActivity::class.java)
        setButtonClickListener(btnManual, ManualActivity::class.java)

        //weather
        btnWeather.setOnClickListener {
            if (CheckInternet(this).isInternetAvailable()) {
                val intent = Intent(this, LoadScreenActivity::class.java)
                intent.putExtra(LoadScreenActivity.EXTRA_TARGET_ACTIVITY, WeatherActivity::class.java.name) // loads loading screen before targetActivity
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else {
                showAlertThatInternetIsNotAvailable()
            }
        }

        //settings
        btnSettings.setOnClickListener { view ->
            showSettingsMenu(view)
        }

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter.isEnabled) {
            btnSwitch.isChecked = !btnSwitch.isChecked
        }

        //bluetooth connect
        btnBtConnect.setOnClickListener {

            // Perform actions based on the new state of the switch
            if (!btnSwitch.isChecked) {
                // Code to execute when the switch is turned ON
                try {
                    // Your Bluetooth connection code here...
                    requestBluetoothPermissions()
                } catch (e: RuntimeException) {
                    showAlertToTurnOnBluetooth()
                } catch (e: SecurityException) {
                    showAlertToTurnOnBluetooth()
                }
            } else {
                if (!bluetoothAdapter.isEnabled) {
                    btnSwitch.isChecked = !btnSwitch.isChecked
                } else {
                    val intent = Intent(this, SoilActivityTest::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    /*********************************
     * D. Settings Menu Logistics
     *-------------------------------*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    private fun showSettingsMenu(view: View?) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.settings_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.btn_sync_database -> {
                    // Handle sync action
                    if (CheckInternet(this).isInternetAvailable()) { showSyncDatabaseDialog() }
                    else { showAlertThatInternetIsNotAvailable() }
                    true
                }
                R.id.btn_language -> {
                    // Handle change language action
                    showLanguageDialog()
                    true
                }
                R.id.btn_logout -> {
                    // Handle logout action
                    logoutUser()
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
                if (CheckInternet(this).isInternetAvailable()) { showSyncDatabaseDialog() }
                else { showAlertThatInternetIsNotAvailable() }
                true
            }
            R.id.btn_language -> {
                showLanguageDialog()
                true
            }
            R.id.btn_logout -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*********************************
     * D.1 Settings - Sync Database
     *-------------------------------*/
    private fun showSyncDatabaseDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.RoundedAlertDialog)
        dialogBuilder.setMessage(R.string.dialog_sync_database)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_sync_database_button) { dialog, id ->
                // Perform the database sync operation
                val dbHelper = SQLiteModel(this)
                dbHelper.syncDataWithFirebase(auth)
                Toast.makeText(this, R.string.dialog_sync_database_result, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.dialog_cancel_button) { dialog, id ->
                // Close the dialog
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Database Sync")
        alert.show()
    }

    /*********************************
     * D.2 Settings - Change Language
     *-------------------------------*/
    private fun setLocale(languageCode: String) {
        // Save the chosen language in SharedPreferences or any persistent storage
        // so you can load it on app start next time.
        val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
    }
    private fun showLanguageDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.RoundedAlertDialog)
        val languages = arrayOf("English", "Tagalog")
        var chosenLanguage = 0 // 0 for English, 1 for Tagalog

        dialogBuilder.setSingleChoiceItems(languages, chosenLanguage) { dialog, which ->
            chosenLanguage = which
        }

        dialogBuilder.setCancelable(false)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, id ->
                if (chosenLanguage == 1) {
                    setLocale("fil") // Tagalog
                    recreate() // Recreate the activity to apply the language change
                } else {
                    setLocale("en") // English or default language
                    recreate()
                }
            }
            .setNegativeButton(R.string.dialog_cancel_button) { dialog, id ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(R.string.dialog_set_language)
        alert.show()
    }

    /*********************************
     * D.3 Settings - Logout User
     *-------------------------------*/
    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    /*********************************
     * E. BLUETOOTH LOGISTICS
     *-------------------------------*/
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 101
        private const val REQUEST_ENABLE_BT = 1
    }

    /**-----------------------------------
     * E.1 Request Bluetooth Permission */
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if the BLUETOOTH_CONNECT permission is already granted
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request the BLUETOOTH_CONNECT permission
                showAlertAskingForBluetoothPermission()
            } else {
                // Permission is granted, you can initiate Bluetooth operations here
                connectToBluetoothDevice()
            }
        } else {
            // For older versions, you can directly initiate Bluetooth operations as BLUETOOTH_CONNECT permission is not required
            connectToBluetoothDevice()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, initiate Bluetooth operations
                connectToBluetoothDevice()
            } else {
                // Permission was denied, show an AlertDialog
                showAlertForDenyingBluetoothPermission()
            }
        }
    }

    /**-----------------------------------
     * E.2 Turn On Bluetooth Permission */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            // User agreed to enable Bluetooth, start the activity and toggle the switch
            btnSwitch.isChecked = true
            val intent = Intent(this, SoilActivityTest::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            // User refused to enable Bluetooth, toggle the switch back to off
            btnSwitch.isChecked = false
        }
    }


    /**-----------------------------------
     * E.3 Connect to Bluetooth Device  */
    private fun connectToBluetoothDevice() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.dialog_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            showAlertToTurnOnBluetooth()
            return
        } else {
            btnSwitch.isChecked = !btnSwitch.isChecked
        }

        //TODO: Add MAC Address if possible. Remove if not.
        val deviceAddress = "00:11:22:33:AA:BB" // MAC address of the Bluetooth device
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Consider calling ActivityCompat#requestPermissions to request the missing permissions.
                return
            }

            // Here you would actually connect to the device. This could involve creating a socket, for example.
            // For simplicity, we're just showing a message.
            Toast.makeText(this, "Connecting to device: $deviceAddress", Toast.LENGTH_SHORT).show()

            // Assuming you have a method to connect to the device
            // connectToDevice(device)
        } catch (e: IOException) {
            Toast.makeText(this, "Error connecting to device: $e", Toast.LENGTH_SHORT).show()
        }
    }

    /**----------------------------
     * E.4 Alerts for Bluetooth  */
    private fun showAlertAskingForBluetoothPermission() {
        AlertDialog.Builder(this, R.style.RoundedAlertDialog)
            .setTitle(R.string.dialog_bluetooth_permission_title)
            .setMessage(R.string.dialog_bluetooth_permission_content)
            .setPositiveButton(R.string.dialog_i_understand) { dialog, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_PERMISSION)
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showAlertForDenyingBluetoothPermission() {
        AlertDialog.Builder(this, R.style.RoundedAlertDialog)
            .setTitle(R.string.dialog_bluetooth_denied_title)
            .setMessage(R.string.dialog_bluetooth_denied_content)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel_button) { dialog, _ ->
                dialog.dismiss()
                // Optional: handle cancellation.
            }
            .create()
            .show()
    }

    private fun showAlertToTurnOnBluetooth() {
        AlertDialog.Builder(this, R.style.RoundedAlertDialog)
            .setTitle(R.string.dialog_bluetooth_off_title)
            .setMessage(R.string.dialog_bluetooth_off_content)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                dialog.dismiss()
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request the Bluetooth connect permission
                    requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ENABLE_BT)
                } else {
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
            }
            .create()
            .show()
    }

    private fun showAlertThatInternetIsNotAvailable() {
        AlertDialog.Builder(this, R.style.RoundedAlertDialog)
            .setTitle(R.string.dialog_internet_connection_title)
            .setMessage(R.string.dialog_internet_connection_content)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}