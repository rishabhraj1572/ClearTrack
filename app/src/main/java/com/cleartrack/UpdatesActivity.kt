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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdatesActivity : AppCompatActivity() {


    private lateinit var adapter: UpdatesAdapter
    val db = Firebase.firestore

    val items: ArrayList<UpdateItem> = arrayListOf()

    private lateinit var logistic : String
    private lateinit var orderId : String
    lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    private lateinit var showdetails : Button

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

        adapter = UpdatesAdapter(items)
        adapter.setOnItemClickListener(object : UpdatesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
 // :)
                val clickedItem = items[position]
                val location = clickedItem.location
                val logisctics = clickedItem.logistics
                val pincode = clickedItem.pincode
                val time = clickedItem.text4
                val email = clickedItem.email
                val phone = clickedItem.phone
                val status = clickedItem.status

                val i = Intent(this@UpdatesActivity,InformationActivity::class.java)
                i.putExtra("location",location)
                i.putExtra("logistics",logisctics)
                i.putExtra("pincode",pincode)
                i.putExtra("time",time)
                i.putExtra("email",email)
                i.putExtra("phone",phone)
                i.putExtra("status",status)
//

                startActivity(i)

            }
        })
        recyclerView.adapter = adapter

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

            db.collection("users").document(userId).get().addOnSuccessListener {
                value->
                val location : String = value.get("address").toString()
                val company : String = value.get("company").toString()
                val pincode : String = value.get("pincode").toString()


                val currentTime = System.currentTimeMillis().toString()
                val email : String=value.get("email").toString()
                val phonenumber : String=value.get("phone").toString()


                getStatus(pincode) { status ->
                    val updateValues = mapOf(
                        "location" to location,
                        "logistics" to company,
                        "pincode" to pincode,
                        "time" to currentTime,
                        "email" to email,
                        "phone" to phonenumber,
                        "status" to status
                    )

                    db.collection("orders").document(orderId).collection("updates")
                        .document(currentTime).set(updateValues).addOnSuccessListener {

//                            items.clear()
                            adapter.notifyDataSetChanged()
//                            showUpdates(orderId)
                        }
                }

            }

        }

    }

    private fun getStatus(pincode: String, callback: (String) -> Unit) {
        if (items.isNotEmpty()) {
            // Fetch the order's destination pincode
            db.collection("orders").document(orderId).get().addOnSuccessListener { task ->
                val destinationPincode = task.getString("pincode")

                // Check if the current pincode matches the destination
                if (destinationPincode == pincode) {
                    // If completed, update `is_active` to false in Firestore
                    db.collection("orders").document(orderId).update("is_active", false)
                        .addOnSuccessListener {
                            Log.d("OrderStatus", "is_active set to false for orderId: $orderId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("OrderStatus", "Failed to update is_active for orderId: $orderId", e)
                        }

                    callback("Completed")
                    return@addOnSuccessListener
                }

                // Retrieve details of the last item
                val lastItem = items.last()
                val secondLastItem = if (items.size > 1) items[items.size - 2] else null

                // Logic for updating the status based on the last and second-last items
                val status = when {
                    secondLastItem != null &&
                            lastItem.location == secondLastItem.location &&
                            lastItem.logistics == secondLastItem.logistics &&
                            lastItem.pincode == secondLastItem.pincode -> {
                        if (lastItem.status == "Dispatched") "Received" else "Dispatched"
                    }
                    else -> "Received"
                }

                callback(status)
            }
        } else {
            // Default status if there are no previous updates
            callback("Received")
        }
    }



    private fun showUpdates(orderId: String) {
        db.collection("orders")
            .document(orderId)
            .collection("updates")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ERROR", "Failed to fetch updates: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (change in snapshot.documentChanges) {
                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                val document = change.document
                                val location = "Location : " + document.getString("location").orEmpty()
                                val logistic = "Logistics : " + document.getString("logistics").orEmpty()
                                val pincode = "Pin Code : " + document.getString("pincode").orEmpty()
                                val status = "Status : " + document.getString("status").orEmpty()
                                val time = document.getString("time").orEmpty()
                                val email = document.getString("email").orEmpty()
                                val phoneNumber = document.getString("phone").orEmpty()

                                val t: Long = time.toLongOrNull() ?: 0L
                                val formatter = SimpleDateFormat("hh:mm a, dd-MM-yyyy", Locale.getDefault())
                                val formattedTime = "Time : " + formatter.format(Date(t))

                                val newItem = UpdateItem(document.id,location, logistic, pincode, status, formattedTime, t, phoneNumber, email)
                                items.add(newItem)
                                adapter.notifyItemInserted(items.size - 1)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val document = change.document
                                val updatedItemIndex = items.indexOfFirst { it.documentId == document.id } // Use unique ID
                                if (updatedItemIndex != -1) {
                                    val updatedItem = items[updatedItemIndex]
                                    updatedItem.location = "Location : " + document.getString("location").orEmpty()
                                    updatedItem.logistics = "Logistics : " + document.getString("logistics").orEmpty()
                                    updatedItem.pincode = "Pin Code : " + document.getString("pincode").orEmpty()
                                    updatedItem.status = "Status : " + document.getString("status").orEmpty()
                                    updatedItem.time = document.getString("time").orEmpty().toLong()
                                    updatedItem.email=document.getString("email").orEmpty()
                                    updatedItem.phone=document.getString("phone").orEmpty()

                                    // Update other fields
                                    adapter.notifyItemChanged(updatedItemIndex)
                                }
                            }
                            DocumentChange.Type.REMOVED -> {
                                val removedItemIndex = items.indexOfFirst { it.documentId == change.document.id } // Use unique ID
                                if (removedItemIndex != -1) {
                                    items.removeAt(removedItemIndex)
                                    adapter.notifyItemRemoved(removedItemIndex)
                                    adapter.notifyDataSetChanged() // Force refresh
                                }
                            }
                        }
                    }

                    items.sortBy { it.time }
                }
            }
    }


}
