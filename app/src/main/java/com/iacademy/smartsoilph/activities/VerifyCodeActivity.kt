package com.iacademy.smartsoilph.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.iacademy.smartsoilph.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyCodeActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_page)

        auth = FirebaseAuth.getInstance()

        // Retrieve the verification ID from the Intent's extras
        verificationId = intent.getStringExtra("verificationId") ?: ""

        val codeInput = findViewById<EditText>(R.id.verificationCodeEditText) // Updated ID
        val verifyButton = findViewById<Button>(R.id.submitButton) // Updated ID

        verifyButton.setOnClickListener {
            val code = codeInput.text.toString().trim()
            if (code.isNotEmpty()) {
                verifyVerificationCode(code)
            } else {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyVerificationCode(code: String) {
        // Create a PhoneAuthCredential with the code
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        // Signing in the user with the credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Verification successful we will start the profile activity
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Verification failed
                        Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
