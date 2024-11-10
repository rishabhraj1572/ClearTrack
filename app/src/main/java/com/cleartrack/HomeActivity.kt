package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {


    val db = Firebase.firestore
    lateinit var userid : String

    private lateinit var auth: FirebaseAuth
    lateinit var isLogistic : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()

        userid = auth.uid.toString()


        var logout: Button = findViewById(R.id.logout)

        db.collection("users").document(userid).get().addOnSuccessListener {
            task ->
            isLogistic = task.get("is_logistic_partner").toString()
           // Log.e("islogistic", isLogistic.toString())


        }

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
            //startActivity(Intent(this,QRScanner::class.java))
            val intent = Intent(this,QRScanner::class.java)
            intent.putExtra("isLogistic",isLogistic)
            startActivity(intent)

        }
    }
}