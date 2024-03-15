package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.models.SQLiteModel

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var firebaseModel: FirebaseModel

    private lateinit var ivLock: ImageView
    private lateinit var ivEmail: ImageView

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
        ivLock = findViewById(R.id.iv_lock)
        ivEmail = findViewById(R.id.iv_email)
        emailEditText = findViewById(R.id.editTextMobileNumber)  // Replace with your actual EditText ID for email
        passwordEditText = findViewById(R.id.editTextPassword)  // Replace with your actual EditText ID for password
        val loginButton = findViewById<CardView>(R.id.buttonSignIn)  // Replace with your actual Button ID

        loginButton.setOnClickListener {
            loginUser()
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

        // Proceed with Firebase authentication
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
    }

    private fun setupRegisterTextView() {
        val registerTextView = findViewById<TextView>(R.id.tv_sign)
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupForgotTextView() {
        val registerTextView = findViewById<TextView>(R.id.tv_forgot)
        registerTextView.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToMainActivity() {
        // Navigate to Main Activity
        val intent = Intent(this, HomeActivity::class.java)  // Replace MainActivity with your main activity class
        startActivity(intent)
        finish()
    }

    private fun checkIfEmailVerified() {
        val user = auth.currentUser

        if (user != null && user.isEmailVerified) {
            // Fetch and save all user data from Firebase
            firebaseModel.getAllUserDataFromFirebase(auth, this)

            // Email is verified, proceed to the main activity
            val intent = Intent(this, HomeActivity::class.java) // Replace with your main activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(baseContext, "Please verify your email first.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetDatabase() {
        val dbHelper = SQLiteModel(this)
        dbHelper.deleteDatabase()
        // Show a Toast message confirming the database reset
        Toast.makeText(this, "Database has been reset", Toast.LENGTH_SHORT).show()
    }
}
