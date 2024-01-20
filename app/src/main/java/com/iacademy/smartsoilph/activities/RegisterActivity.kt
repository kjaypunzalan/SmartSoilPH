package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize EditTexts
        emailEditText = findViewById(R.id.editTextMobileNumber) // Assuming this is for the email
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword)

        // Initialize the sign-up button
        val signUpButton = findViewById<Button>(R.id.buttonSignUp)
        signUpButton.setOnClickListener {
            signUpUser()
        }

        setupLoginTextView()
    }

    private fun signUpUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()
                    // Redirect to login or main activity
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (currentUser?.isEmailVerified == true) {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    } else {
                        currentUser?.sendEmailVerification()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Please check your email to verify your account",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setupLoginTextView() {
        val loginTextView = findViewById<TextView>(R.id.textViewSignIn)
        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
