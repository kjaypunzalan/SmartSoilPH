package com.iacademy.smartsoilph.activities

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.onboarding.ViewPagerAdapter
import com.iacademy.smartsoilph.onboarding.screens.FifthScreen
import com.iacademy.smartsoilph.onboarding.screens.FirstScreen
import com.iacademy.smartsoilph.onboarding.screens.FourthScreen
import com.iacademy.smartsoilph.onboarding.screens.SecondScreen
import com.iacademy.smartsoilph.onboarding.screens.ThirdScreen

class OnboardingActivity : BaseActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_view_pager)

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
}
