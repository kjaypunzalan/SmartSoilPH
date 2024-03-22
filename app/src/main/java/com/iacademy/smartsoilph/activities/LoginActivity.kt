package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.utils.CheckInternet

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var firebaseModel: FirebaseModel

    private lateinit var ivLock: ImageView
    private lateinit var ivEmail: ImageView

    private lateinit var cvQuestion: CardView
    private lateinit var cvWeb: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page2)

        setupRegisterTextView()
        setupForgotTextView()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        firebaseModel = FirebaseModel()
        resetDatabase()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            // User is already logged in, navigate to the main activity
            navigateToMainActivity()
        }

        // Initialize views
        cvWeb = findViewById(R.id.cv_web)

        ivLock = findViewById(R.id.iv_lock)
        ivEmail = findViewById(R.id.iv_email)
        emailEditText = findViewById(R.id.editTextMobileNumber)  // Replace with your actual EditText ID for email
        passwordEditText = findViewById(R.id.editTextPassword)  // Replace with your actual EditText ID for password
        val loginButton = findViewById<CardView>(R.id.buttonSignIn)  // Replace with your actual Button ID

        loginButton.setOnClickListener {
            loginUser()
        }

        cvWeb.setOnClickListener {
            showLanguageDialog()

        }


        // Set focus change listener on the EditTextEmail
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText is focused, change the ImageView tint
                ivEmail.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                // EditText lost focus, remove the tint or set it to default
                ivEmail.clearColorFilter()
            }
        }

        // Set focus change listener on the EditTextPassword
        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText is focused, change the ImageView tint
                ivLock.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                // EditText lost focus, remove the tint or set it to default
                ivLock.clearColorFilter()
            }
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Check if email is empty
        if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            emailEditText.requestFocus()
            return
        }

        // Check if password is empty
        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty"
            passwordEditText.requestFocus()
            return
        }

        // Check if email is valid
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email address"
            emailEditText.requestFocus()
            return
        }

        // Check if connected to the internet
        if (CheckInternet(this).isInternetAvailable()) {
            // Proceed with Login if all validations pass
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success, update UI with the signed-in user's information
                        checkIfEmailVerified()
                    } else {
                        // If login fails, display a message to the user.
                        Toast.makeText(baseContext, "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            AlertDialog.Builder(this, R.style.RoundedAlertDialog)
                .setTitle(R.string.dialog_internet_connection_title)
                .setMessage(R.string.dialog_internet_connection_content)
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun setupRegisterTextView() {
        val registerTextView = findViewById<TextView>(R.id.tv_sign)
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupForgotTextView() {
        val registerTextView = findViewById<TextView>(R.id.tv_forgot)
        registerTextView.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToMainActivity() {
        // Navigate to Main Activity
        val intent = Intent(this, HomeActivity::class.java)  // Replace MainActivity with your main activity class
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun checkIfEmailVerified() {
        val user = auth.currentUser

        if (user != null && user.isEmailVerified) {
            // Fetch and save all user data from Firebase
            firebaseModel.getAllUserDataFromFirebase(auth, this)

            // Email is verified, proceed to the main activity
            val intent = Intent(this, LoadScreenActivity::class.java)
            intent.putExtra(LoadScreenActivity.EXTRA_TARGET_ACTIVITY, HomeActivity::class.java.name) // loads loading screen before targetActivity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(baseContext, R.string.dialog_login_verification_failed, Toast.LENGTH_SHORT).show()
        }
    }

    /*********************************
     * Change Language
     *-------------------------------*/
    private fun setLocale(languageCode: String) {
        // Save the chosen language in SharedPreferences or any persistent storage
        // so you can load it on app start next time.
        val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
    }
    private fun showLanguageDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.RoundedAlertDialog)
        val languages = arrayOf("English", "Tagalog")
        var chosenLanguage = 0 // 0 for English, 1 for Tagalog

        dialogBuilder.setSingleChoiceItems(languages, chosenLanguage) { dialog, which ->
            chosenLanguage = which
        }

        dialogBuilder.setCancelable(false)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, id ->
                if (chosenLanguage == 1) {
                    setLocale("fil") // Tagalog
                    recreate() // Recreate the activity to apply the language change
                } else {
                    setLocale("en") // English or default language
                    recreate()
                }
            }
            .setNegativeButton(R.string.dialog_cancel_button) { dialog, id ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(R.string.dialog_set_language)
        alert.show()
    }

    private fun resetDatabase() {
        val dbHelper = SQLiteModel(this)
        dbHelper.deleteDatabase()
    }
}
