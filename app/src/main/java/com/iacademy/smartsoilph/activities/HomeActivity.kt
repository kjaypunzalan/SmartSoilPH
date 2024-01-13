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

        val btnSoil = findViewById<Button>(R.id.btnSoil);
        btnSoil.setOnClickListener {
            val Intent = Intent(this, SoilActivity::class.java);
            startActivity(Intent);
        }

        val btnWeather = findViewById<Button>(R.id.btnWeather);
        btnWeather.setOnClickListener {
            val Intent = Intent(this, WeatherActivity::class.java);
            startActivity(Intent);
        }

        val btnReports = findViewById<Button>(R.id.btnReports);
        btnReports.setOnClickListener {
            val Intent = Intent(this, SoilMonitoringActivity::class.java);
            startActivity(Intent);
        }
    }
}