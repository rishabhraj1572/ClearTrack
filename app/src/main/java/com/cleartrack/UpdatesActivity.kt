package com.cleartrack

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdatesActivity : AppCompatActivity() {


    private lateinit var adapter: UpdatesAdapter
    val db = Firebase.firestore

    val items: ArrayList<UpdateItem> = arrayListOf()

    private lateinit var logistic : String
    private lateinit var showdetails : Button
    private lateinit var orderId : String
    lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth
    private var userId: String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        auth = FirebaseAuth.getInstance()
        userId = auth.uid.toString()


        logistic = intent.getStringExtra("isLogistic").toString()
        orderId = intent.getStringExtra("orderId").toString()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        showdetails=findViewById<Button>(R.id.showdetails)

        showUpdates(orderId)

        val updateBtn : Button = findViewById(R.id.update)

        if(logistic=="true") {
            updateBtn.visibility = View.VISIBLE
        } else {
            updateBtn.visibility=View.GONE
        }

        showdetails.setOnClickListener {
            val intent = Intent(this, ShowDetailsActiivty::class.java)
            intent.putExtra("orderId",orderId)
            startActivity(intent)
        }

        updateBtn.setOnClickListener{

            db.collection("users").document(userId!!).get().addOnSuccessListener {
                value->
                val location : String = value.get("address").toString()
                val company : String = value.get("company").toString()
                val pincode : String = value.get("pincode").toString()

                val currentTime = System.currentTimeMillis().toString()

                val updateValues = mapOf(
                    "location" to location,
                    "logistics" to company,
                    "pincode" to pincode,
                    "time" to currentTime,
                    "status" to getStatus()
                )

                db.collection("orders").document(orderId).collection("updates")
                    .document(currentTime).set(updateValues).addOnSuccessListener {

                        items.clear()
                        adapter.notifyDataSetChanged()
                        showUpdates(orderId)
                    }

            }


        }



    }

    private fun getStatus(): String {
        if (items.size > 1) {
            val lastItem = items[items.size - 1]
            val secondLastItem = if (items.size > 1) items[items.size - 2] else null

            if (secondLastItem!=null && lastItem == secondLastItem) {
                return "Dispatched"
            }else{
                return "Received"
            }
        }

        return ""

    }

    private fun showUpdates(orderId: String) {
        db.collection("orders")
            .document(orderId)
            .collection("updates")
            .get()
            .addOnSuccessListener { task ->
                items.clear()
                for (document in task) {
                    val location = document.get("location").toString()
                    val logistic = document.get("logistic").toString()
                    val pincode = document.get("pincode").toString()
                    val time = document.get("time") as Long

                    val formatter = SimpleDateFormat("hh:mm a, dd-MM-yyyy", Locale.getDefault())
                    val formattedTime = formatter.format(Date(time))
                    items.add(UpdateItem(location, logistic, pincode, formattedTime, time))
                }

                items.sortBy { it.time }

                adapter = UpdatesAdapter(items)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Failed to fetch updates: ${e.message}")
            }
    }

}
