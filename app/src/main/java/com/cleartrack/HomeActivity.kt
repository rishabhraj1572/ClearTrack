package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()

        var logout: Button = findViewById(R.id.logout)

        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finishAffinity()
        }

        var createOrder: Button = findViewById(R.id.createOrder)

        createOrder.setOnClickListener{
            startActivity(Intent(this,CreateOrderActivity::class.java))
        }

        var scanQR: Button = findViewById(R.id.scanQr)

        scanQR.setOnClickListener{
            startActivity(Intent(this,QRScanner::class.java))
        }
    }
}