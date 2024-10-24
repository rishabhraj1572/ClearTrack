package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        if (auth.currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
//        enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            val loginBtn: Button = findViewById(R.id.loginBtn)
            val signupBtn: Button = findViewById(R.id.signupBtn)

            loginBtn.setOnClickListener {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
            }

            signupBtn.setOnClickListener {
                val i = Intent(this, SignupActivity::class.java)
                startActivity(i)
            }


        }
    }
}