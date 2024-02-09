package com.iacademy.smartsoilph.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.iacademy.smartsoilph.R
class FourthScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_fourth_screen, container, false)

        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.findViewById<CardView>(R.id.btn_next3).setOnClickListener() {
            viewPager?.currentItem = 4
        }

        return view
    }

}