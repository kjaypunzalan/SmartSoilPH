package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R

class LoginActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        if (auth.currentUser != null) navigateToMainActivity()

        findViewById<MaterialButton>(R.id.buttonSignIn).setOnClickListener { loginUser() }
        findViewById<TextView>(R.id.tv_sign).setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        findViewById<TextView>(R.id.tv_forgot).setOnClickListener { startActivity(Intent(this, ResetPasswordActivity::class.java)) }
    }

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.editTextMobileNumber).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) checkIfEmailVerified()
                else Toast.makeText(baseContext, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun checkIfEmailVerified() {
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) navigateToMainActivity()
        else Toast.makeText(baseContext, "Please verify your email first.", Toast.LENGTH_SHORT).show()
    }
}