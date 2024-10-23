package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val loginBtn : Button = findViewById(R.id.loginBtn)
        val signupBtn : Button = findViewById(R.id.signupBtn)

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