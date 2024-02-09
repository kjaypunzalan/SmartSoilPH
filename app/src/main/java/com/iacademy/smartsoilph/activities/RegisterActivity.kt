package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.UserData
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.models.SQLiteModel

class RegisterActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button // Assuming there is a register button
    private lateinit var loginButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize EditTexts and Button
        nameEditText = findViewById(R.id.editTextName) // Update ID as per your layout
        numberEditText = findViewById(R.id.editTextMobileNumber) // Update ID as per your layout
        emailEditText = findViewById(R.id.editTextEmail) // Update ID as per your layout
        passwordEditText = findViewById(R.id.editTextPassword) // Update ID as per your layout
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword) // Update ID as per your layout
        registerButton = findViewById(R.id.buttonSignUp) // Update ID as per your layout
        loginButton = findViewById(R.id.textViewSignIn)

        // Register User
        registerButton.setOnClickListener {
            validateInput()
        }

        // Sign In Button
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Replace with your next activity
            startActivity(intent)
            finish()
        }
    }

    private fun validateInput() {
        val name = nameEditText.text.toString().trim()
        val number = numberEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate name to not contain numbers and special characters
        if (name.isEmpty()) {
            nameEditText.error = "Name cannot be empty"
            nameEditText.requestFocus()
            return
        }
        if (!name.matches("^[A-Za-z\\s]+\$".toRegex())) {
            nameEditText.error = "Name must only contain letters and spaces"
            nameEditText.requestFocus()
            return
        }

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

        // Validate number
        if (number.isEmpty()) {
            numberEditText.error = "Number cannot be empty"
            numberEditText.requestFocus()
            return
        }
        if (number.length != 11) {
            numberEditText.error = "Number must be 11 digits"
            numberEditText.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty"
            passwordEditText.requestFocus()
            return
        }
        if (password.length < 8 || !password.matches(".*[A-Z].*".toRegex()) ||
            !password.matches(".*[a-z].*".toRegex()) || !password.matches(".*[0-9].*".toRegex())) {
            passwordEditText.error = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one number"
            passwordEditText.requestFocus()
            return
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirm Password cannot be empty"
            confirmPasswordEditText.requestFocus()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(baseContext, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with registration if all validations pass
        registerUser(name, number.toDouble(), email, password)
    }

    private fun registerUser(name: String, number: Double, email: String, password: String) {
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

                    //Add to Database
                    val userData = UserData(name, email, number)
                    SQLiteModel(this).addUserData(userData)  // Save to SQLite
                    FirebaseModel().writeNewUser(userData, auth)    // Save to Firebase
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
