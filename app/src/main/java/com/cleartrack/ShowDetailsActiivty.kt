package com.cleartrack

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore

class ShowDetailsActiivty : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var orderId : String
    private lateinit var image : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details_actiivty)

        db = FirebaseFirestore.getInstance()
        orderId = intent.getStringExtra("orderId").toString()
        fetchDataFromFirestore()

        Log.e("Order id",orderId)

    }

    private fun fetchDataFromFirestore() {
        val userRef =
            db.collection("orders").document(orderId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.get("name").toString()
                    val phoneNumber = document.get("phone") .toString()
                    val email = document.get("email").toString()
                    val expAddress = document.get("exporter_address").toString()
                    val impAddress = document.get("importer_address").toString()
                    val impName = document.get("importer_name").toString()
                    val impPhone = document.get("importer_number").toString()
                    val productDesc = document.get("product_description").toString()
                    val userimage = document.get("user_image").toString()
                    findViewById<TextView>(R.id.fullName).text = fullName
                    findViewById<TextView>(R.id.phoneNumber).text = phoneNumber
                    findViewById<TextView>(R.id.email).text = email
                    findViewById<TextView>(R.id.exp_address).text = expAddress
                    findViewById<TextView>(R.id.imp_address).text = impAddress
                    findViewById<TextView>(R.id.imp_name).text = impName
                    findViewById<TextView>(R.id.imp_phone).text = impPhone
                    findViewById<TextView>(R.id.product_desc).text = productDesc

                    Glide.with(this)
                        .load(userimage)
                        .apply(
                            RequestOptions()
                            .override(150, 150)
                            .centerCrop())
                        .into(findViewById<ImageView>(R.id.select_photo))

                } else {
                    Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}