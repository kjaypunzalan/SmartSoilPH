package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.iacademy.smartsoilph.R
import java.util.Random

class LoadScreenActivity : BaseActivity() {
        companion object {
            const val EXTRA_TARGET_ACTIVITY = "extra_target_activity"
        }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_load_screen)

            val targetActivityClassName = intent.getStringExtra(EXTRA_TARGET_ACTIVITY)
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val targetActivityClass = Class.forName(targetActivityClassName)
                    val targetIntent = Intent(this, targetActivityClass)
                    startActivity(targetIntent)
                    finish() // Finish LoadScreenActivity so the user can't return to it.
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    finish() // In case of error, finish the LoadScreenActivity.
                }
            }, 2500) // Delay for the load screen, here set to 2 seconds

    }
}