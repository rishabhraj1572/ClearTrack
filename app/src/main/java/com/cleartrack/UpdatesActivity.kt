package com.cleartrack

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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

    private lateinit var progressBar: ProgressDialog

    private lateinit var showdetails : Button

    private lateinit var orderIdView: TextView

    private lateinit var noItemsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        auth = FirebaseAuth.getInstance()
        userId = auth.uid.toString()

        noItemsText = findViewById(R.id.noItems)

        progressBar = ProgressDialog(this)
        progressBar.setTitle("Loading...")

        logistic = intent.getStringExtra("isLogistic").toString()
        orderId = intent.getStringExtra("orderId").toString()

        orderIdView= findViewById(R.id.orderId)
        orderIdView.text = "Order ID : "+ orderId

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

        val updateBtn: Button = findViewById(R.id.update)
        updateBtn.visibility = View.GONE

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val isLogisticPartner = document.getBoolean("is_logistic_partner") ?: false
                if (isLogisticPartner) {
                    updateBtn.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to fetch user role: ${exception.message}")
            }


        showdetails.setOnClickListener {
            val intent = Intent(this, ShowDetailsActiivty::class.java)
            intent.putExtra("orderId",orderId)
            startActivity(intent)
        }

        updateBtn.setOnClickListener{

            progressBar.show()

            db.collection("users").document(userId).get().addOnSuccessListener {
                value->
                val location : String = value.get("address").toString()
                val company : String = value.get("company").toString()
                val pincode : String = value.get("pincode").toString()


                val currentTime = System.currentTimeMillis().toString()
                val email : String=value.get("email").toString()
                val phonenumber : String=value.get("phone").toString()


                getStatus(pincode, location, company) { status ->
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

    private fun getStatus(pincode: String, location: String, company: String, callback: (String) -> Unit) {

        db.collection("orders").document(orderId).get().addOnSuccessListener { task ->
            val destinationPincode = task.getString("pincode")


            if (destinationPincode == pincode) {

                db.collection("orders").document(orderId)
                    .update("is_active", false)
                    .addOnSuccessListener {
                        Log.d("OrderStatus", "Marked as completed")
                    }

                callback("Completed")  // Mark status as completed
                return@addOnSuccessListener
            }


            if (items.isNotEmpty()) {
                val lastItem = items.last()


                if (lastItem.location == "Location : $location" &&
                    lastItem.logistics == "Logistics : $company" &&
                    lastItem.pincode == "Pin Code : $pincode") {

                    // Toggle between "Dispatched" and "Received"
                    val status = if (lastItem.status == "Dispatched") {
                        "Received"
                    } else {
                        "Dispatched"
                    }

                    callback(status)
                } else {
                    callback("Received")
                }
            } else {
                callback("Dispatched") 
            }
        }
    }




    private fun showUpdates(orderId: String) {

        checkItems()

        db.collection("orders")
            .document(orderId)
            .collection("updates")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ERROR", "Failed to fetch updates: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    progressBar.dismiss()
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
                                checkItems()
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
                                    checkItems()
                                }
                            }
                            DocumentChange.Type.REMOVED -> {
                                val removedItemIndex = items.indexOfFirst { it.documentId == change.document.id } // Use unique ID
                                if (removedItemIndex != -1) {
                                    items.removeAt(removedItemIndex)
                                    adapter.notifyItemRemoved(removedItemIndex)
                                    adapter.notifyDataSetChanged() // Force refresh
                                    checkItems()
                                }
                            }
                        }
                    }

                    items.sortBy { it.time }
                }
            }

    }


    private fun checkItems(){
        if(items.isEmpty()){
            progressBar.dismiss()
            noItemsText.visibility = View.VISIBLE
        }else{
            noItemsText.visibility = View.GONE
        }
    }

}
