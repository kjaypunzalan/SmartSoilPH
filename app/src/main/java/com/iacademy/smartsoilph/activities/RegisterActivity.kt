package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button // Assuming there is a register button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize EditTexts and Button
        emailEditText = findViewById(R.id.editTextMobileNumber) // Update ID as per your layout
        passwordEditText = findViewById(R.id.editTextPassword) // Update ID as per your layout
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword) // Update ID as per your layout
        registerButton = findViewById(R.id.buttonSignUp) // Update ID as per your layout

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (password == confirmPassword) {
                registerUser(email, password)
            } else {
                Toast.makeText(baseContext, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Send verification email
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(baseContext, "Verification email sent to $email", Toast.LENGTH_SHORT).show()
                                checkIfEmailVerified()
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkIfEmailVerified() {
        val user = auth.currentUser

        user?.let {
            if (user.isEmailVerified) {
                // Email is verified, proceed to the next activity
                val intent = Intent(this, LoginActivity::class.java) // Replace with your next activity
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(baseContext, "Please verify your email first.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
