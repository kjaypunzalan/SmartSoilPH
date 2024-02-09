package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R

class ResetPasswordActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var tvSignIn: TextView
    private lateinit var sendVerificationButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpassword_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        emailEditText = findViewById(R.id.et_email)
        sendVerificationButton = findViewById(R.id.btn_submit)
        tvSignIn = findViewById(R.id.tv_sign_in)

        sendVerificationButton.setOnClickListener {
            validateInput()
        }

        tvSignIn.setOnClickListener {
            // Intent to start new Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }

    private fun validateInput() {
        val email = emailEditText.text.toString().trim()

        // Validate email
        if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            emailEditText.requestFocus()
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email address"
            emailEditText.requestFocus()
            return
        }

        // Proceed with reset password if all validations pass
        sendPasswordResetEmail(email)
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent \nPlease login using your new password", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        // Intent to start new Activity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Close the current activity
                    }, 3000)
                } else {
                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}