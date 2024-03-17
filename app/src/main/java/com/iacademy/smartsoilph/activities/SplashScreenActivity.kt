package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.onboarding.screens.FirstScreen
import java.util.Random

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val randomDelay = Random().nextInt(1000) + 1500
        Handler(Looper.getMainLooper()).postDelayed({
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }, randomDelay.toLong())

    }
}