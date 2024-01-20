package com.iacademy.smartsoilph.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.iacademy.smartsoilph.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // declare + initialize variables
        val btnSoil = findViewById<Button>(R.id.btnSoil);
        val btnWeather = findViewById<Button>(R.id.btnWeather);
        val btnReports = findViewById<Button>(R.id.btnReports);
        val btnManual = findViewById<Button>(R.id.btnManual);

        // set click listeners for buttons
        setButtonClickListener(btnSoil, SoilActivity::class.java)
        setButtonClickListener(btnWeather, WeatherActivity::class.java)
        setButtonClickListener(btnReports, LoadScreenActivity::class.java)
        setButtonClickListener(btnManual, LoadScreenActivity::class.java)

    }

    //Button Function
    private fun setButtonClickListener(button: Button, activityClass: Class<*>) {
        button.setOnClickListener {
            val intent = Intent(this, activityClass)
            startActivity(intent)
        }
    }
}