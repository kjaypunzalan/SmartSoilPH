package com.iacademy.smartsoilph.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iacademy.smartsoilph.R
class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if the fragment is added to its context
            if (isAdded) {
                if (onBoardingFinished()) {
                    findNavController().navigate(R.id.action_splashFragment_to_loginActivity)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
                }
            }
        }, 3000)

        return view
    }

    private fun onBoardingFinished(): Boolean {
        // Early return if context is null to avoid IllegalStateException
        val context = context ?: return false
        val sharedPref = context.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}
