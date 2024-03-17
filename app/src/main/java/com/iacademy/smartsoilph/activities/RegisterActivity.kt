package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
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
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.UserData
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.models.SQLiteModel
import com.iacademy.smartsoilph.utils.CheckInternet

class RegisterActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: CardView // Assuming there is a register button
    private lateinit var loginButton: TextView
    private lateinit var btnReturn: ImageView

    private lateinit var ivName: ImageView
    private lateinit var ivPhone: ImageView
    private lateinit var ivEmail: ImageView
    private lateinit var ivPass: ImageView
    private lateinit var ivConfirm: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page2)

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
        btnReturn = findViewById(R.id.toolbar_back_icon)

        ivName = findViewById(R.id.iv_name)
        ivPhone = findViewById(R.id.iv_phone)
        ivEmail = findViewById(R.id.iv_email)
        ivPass = findViewById(R.id.iv_password)
        ivConfirm = findViewById(R.id.iv_confirm)


        // Register User
        registerButton.setOnClickListener {
            validateInput()
        }

        // Sign In Button
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Replace with your next activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Set focus change listener on the EditTextName
        nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText is focused, change the ImageView tint
                ivName.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                // EditText lost focus, remove the tint or set it to default
                ivName.clearColorFilter()
            }
        }

        // Set focus change listener on the EditTextPhone
        numberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText is focused, change the ImageView tint
                ivPhone.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                // EditText lost focus, remove the tint or set it to default
                ivPhone.clearColorFilter()
            }
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
                ivPass.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                // EditText lost focus, remove the tint or set it to default
                ivPass.clearColorFilter()
            }
        }

        // Set focus change listener on the EditTextConfirmPassword
        confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText is focused, change the ImageView tint
                ivConfirm.setColorFilter(ContextCompat.getColor(this, R.color.main_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                // EditText lost focus, remove the tint or set it to default
                ivConfirm.clearColorFilter()
            }
        }

        btnReturn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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

        // Check if connected to the internet
        if (CheckInternet(this).isInternetAvailable()) {
            // Proceed with registration if all validations pass
            registerUser(name, number.toDouble(), email, password)
        } else {
            AlertDialog.Builder(this, R.style.RoundedAlertDialog)
                .setTitle("You are not connected to the internet.")
                .setMessage("Please connect to the internet before registering. Thank you!")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun registerUser(name: String, number: Double, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Send verification email
                    val user = auth.currentUser
                    val userID = user?.uid ?: ""    // Generate a unique userID

                    //Add to Database
                    val userData = UserData(userID, name, email, number)
                    SQLiteModel(this).addUserData(userData)  // Save to SQLite
                    FirebaseModel().writeNewUser(userData, auth)    // Save to Firebase

                    // Sign out after registration to prevent auto-login
                    auth.signOut()
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(baseContext, "Verification email sent to $email", Toast.LENGTH_SHORT).show()

                                AlertDialog.Builder(this, R.style.RoundedAlertDialog)
                                    .setTitle("Verification Sent to Your Email!")
                                    .setMessage("We have sent a verification email to your email address: $email. Please verify to login! :)")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()

                                        //Go to LoginActivity
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        startActivity(intent)
                                        finish()
                                    }
                                    .create()
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

}
