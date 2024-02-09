package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        setupRegisterTextView()
        setupForgotTextView()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            // User is already logged in, navigate to the main activity
            navigateToMainActivity()
        }

        // Initialize views
        emailEditText = findViewById(R.id.editTextMobileNumber)  // Replace with your actual EditText ID for email
        passwordEditText = findViewById(R.id.editTextPassword)  // Replace with your actual EditText ID for password
        val loginButton = findViewById<MaterialButton>(R.id.buttonSignIn)  // Replace with your actual Button ID

        loginButton.setOnClickListener {
            loginUser()
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
            // Email is verified, proceed to the main activity
            val intent = Intent(this, HomeActivity::class.java) // Replace with your main activity
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(baseContext, "Please verify your email first.", Toast.LENGTH_SHORT).show()
        }
    }
}
