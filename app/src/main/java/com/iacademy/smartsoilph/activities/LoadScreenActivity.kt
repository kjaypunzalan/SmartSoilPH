package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.iacademy.smartsoilph.R
import java.util.Random

class LoadScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_screen)

        val randomDelay = Random().nextInt(1000) + 1500
        Handler().postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, randomDelay.toLong())

    }
}