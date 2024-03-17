package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.onboarding.ViewPagerAdapter
import com.iacademy.smartsoilph.onboarding.screens.FifthScreen
import com.iacademy.smartsoilph.onboarding.screens.FirstScreen
import com.iacademy.smartsoilph.onboarding.screens.FourthScreen
import com.iacademy.smartsoilph.onboarding.screens.SecondScreen
import com.iacademy.smartsoilph.onboarding.screens.ThirdScreen

class OnboardingActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_view_pager)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            // User is already logged in, navigate to the main activity
            navigateToMainActivity()
        }

        viewPager = findViewById(R.id.viewPager)
        val fragments = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen(),
            FourthScreen(),
            FifthScreen()
        )
        val adapter = ViewPagerAdapter(fragments, supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
    }

    private fun navigateToMainActivity() {
        // Navigate to Main Activity
        val intent = Intent(this, HomeActivity::class.java)  // Replace MainActivity with your main activity class
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
