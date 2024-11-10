package com.cleartrack

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UpdatesActivity : AppCompatActivity() {


    val db = Firebase.firestore

    val items: ArrayList<UpdateItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val updateBtn : Button = findViewById(R.id.update)

        updateBtn.setOnClickListener{
            val orderId="GCvCcyt05XPcOcgceiadWblcLe22_1731254417981"
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
}
