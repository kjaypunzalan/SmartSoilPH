package com.example.smartsoilph

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnSoil = findViewById<Button>(R.id.btnSoil);
        btnSoil.setOnClickListener {
            val Intent = Intent(this, SoilMonitoringActivity::class.java);
            startActivity(Intent);
        }
    }
}