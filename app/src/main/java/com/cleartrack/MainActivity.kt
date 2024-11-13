package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var userid: String
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        userid = auth.uid.toString()

        // Check if the user is already logged in
        if (auth.currentUser != null) {

            val db = Firebase.firestore
            db.collection("users").document(userid).get().addOnSuccessListener {
                    task ->
                var isLogistic = task.get("is_logistic_partner").toString()
                // Log.e("islogistic", isLogistic.toString())

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("is_logistic",isLogistic)
                startActivity(intent)
                finish()

            }

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