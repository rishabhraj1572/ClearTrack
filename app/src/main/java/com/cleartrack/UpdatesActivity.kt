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

                val updateValues = mapOf(
                    "location" to location,
                    "logistics" to company,
                    "pincode" to pincode,
                    "time" to currentTime,
                    "email" to email,
                    "phone" to phonenumber,
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
        if (items.size >= 1) {
            val lastItemLocation = items[items.size - 1].location
            val secondLastItemLocation = if (items.size > 1) items[items.size - 2].location else null
            val lastItemLogistics = items[items.size - 1].logistics
            val secondLastItemLogistics = if (items.size > 1) items[items.size - 2].logistics else null
            val lastItemPincode = items[items.size - 1].pincode
            val secondLastItemPincode = if (items.size > 1) items[items.size - 2].pincode else null
            val lastItemStatus = items[items.size - 1].status
            val secondLastItemStatus = if (items.size > 1) items[items.size - 2].status else null

            if (secondLastItemLocation!=null &&
                secondLastItemLogistics!=null&&
                secondLastItemPincode!=null&&
                lastItemLocation==secondLastItemLocation&&
                lastItemLogistics==secondLastItemLogistics&&
                lastItemPincode==secondLastItemPincode) {

                if(lastItemStatus == "Dispatched"){
                    return "Received"
                }else{
                    return "Dispatched"
                }

            } else{
                return "Received"
            }
        }

        return "Dispatched"

    }

    private fun showUpdates(orderId: String) {
        db.collection("orders")
            .document(orderId)
            .collection("updates")
            .get()
            .addOnSuccessListener { task ->
                items.clear()
                for (document in task) {
                    val location = "Location : "+ document.get("location").toString()
                    val logistic = "Logisctics : "+document.get("logistics").toString()
                    val pincode = "Pin Code : "+document.get("pincode").toString()
                    val status = "Status : "+document.get("status").toString()
                    val time : String = document.get("time").toString()
                    val email : String = document.get("email").toString()
                    val phonenumber : String = document.get("phone").toString()

                    val t : Long= time.toLong()

                    val formatter = SimpleDateFormat("hh:mm a, dd-MM-yyyy", Locale.getDefault())
                    val formattedTime = "Time : "+formatter.format(Date(t))
                    items.add(UpdateItem(location, logistic, pincode,status, formattedTime, t, phonenumber,email))
                }

                items.sortBy { it.time }

               // adapter = UpdatesAdapter(items)
                adapter.notifyDataSetChanged()
                //recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Failed to fetch updates: ${e.message}")
            }
    }

}
