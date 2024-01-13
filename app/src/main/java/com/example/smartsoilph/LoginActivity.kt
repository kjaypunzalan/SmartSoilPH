package com.example.smartsoilph

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val mobileNumberInput = findViewById<EditText>(R.id.editTextMobileNumber)
        val signInButton = findViewById<MaterialButton>(R.id.buttonSignIn)

        signInButton.setOnClickListener {
            val mobileNumber = mobileNumberInput.text.toString().trim()

            if (mobileNumber.isNotEmpty()) {
                loginUser(mobileNumber)
            } else {
                Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(mobileNumber: String) {

        val formattedNumber = if (!mobileNumber.startsWith("+63")) {
            "+63$mobileNumber"
        } else {
            mobileNumber
        }
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedNumber)     // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
            .setActivity(this)                   // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(baseContext, "Verification failed: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String,
                                        token: PhoneAuthProvider.ForceResendingToken) {
                    Toast.makeText(baseContext, "Verification code sent.",
                        Toast.LENGTH_SHORT).show()

                    // Save verification ID and resending token so we can use them later
                    val intent = Intent(this@LoginActivity, VerifyCodeActivity::class.java).apply {
                        putExtra("verificationId", verificationId)
                    }
                    startActivity(intent)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Authentication successful.",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
