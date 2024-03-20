package com.iacademy.smartsoilph.activities

import android.content.Context
import android.os.Bundle
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyLanguageContext()
    }

    private fun applyLanguageContext() {
        val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en") // Default to English
        setLocale(this, language!!)
    }

    companion object {
        fun setLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)

            return context.createConfigurationContext(config)
        }

        private fun updateBaseContextLocale(context: Context?): Context? {
            val sharedPreferences = context?.getSharedPreferences("Settings", MODE_PRIVATE)
            val language = sharedPreferences?.getString("My_Lang", "en") // Default to English
            return setLocale(context!!, language!!)
        }
    }
}
