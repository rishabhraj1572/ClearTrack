package com.cleartrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UpdatesActivity : AppCompatActivity() {


    val db = Firebase.firestore

    val items: ArrayList<UpdateItem> = arrayListOf()

    private lateinit var logistic : String
    private lateinit var orderId : String
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        logistic = intent.getStringExtra("isLogistic").toString()
        orderId = intent.getStringExtra("orderId").toString()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        showUpdates(orderId)

        val updateBtn : Button = findViewById(R.id.update)

        if(logistic=="true") {
            updateBtn.visibility = View.VISIBLE
        } else {
            updateBtn.visibility=View.GONE
        }

        updateBtn.setOnClickListener{


        }



    }

    private fun showUpdates(orderId: String) {
        db.collection("orders").document(orderId).collection("updates").get()
            .addOnSuccessListener{ task ->

                for (document in task) {
                    val location = document.get("location").toString()
                    val logistic = document.get("logistic").toString()
                    val pincode = document.get("pincode").toString()
                    val time = document.get("time").toString()

                    Log.e("LOCATION",location)

                    items.add(UpdateItem(location, logistic, pincode, time))
                }
                val adapter = UpdatesAdapter(items)
                recyclerView.adapter = adapter
            }
    }
}
