package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnBack: Button

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.emailEditText)
        etPass = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.loginBtn)
        btnBack = findViewById(R.id.back)

        val back: Button = findViewById(R.id.back)

        auth = FirebaseAuth.getInstance()

        back.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        // Validating inputs
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                    val intent =
                        Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    // Handle specific exceptions
                    val exception = task.exception
                    when (exception) {
                        //incorrect passsword
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, "Incorrect Password. Please try again.", Toast.LENGTH_SHORT).show()
                        }

                        //no user found
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(this, "No account found with this email.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Log In Failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }
}