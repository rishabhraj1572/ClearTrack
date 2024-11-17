package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MyOrdersActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    val items: ArrayList<MyOrderItem> = arrayListOf()
    private lateinit var userId: String
    private lateinit var adapter: MyOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myorder)

        auth = FirebaseAuth.getInstance()
        userId = auth.uid.toString()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MyOrderAdapter(items)
        adapter.setOnItemClickListener(object : MyOrderAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

                val clickedItem = items[position]
                val orderId = clickedItem.orderId
                val status = clickedItem.status
                val qr_url = clickedItem.qr_url

                val i = Intent(this@MyOrdersActivity,MyOrderInformationActivity::class.java)
                i.putExtra("orderId",orderId)
                i.putExtra("status",status)
                i.putExtra("qr_url",qr_url)

                startActivity(i)

            }
        })
        recyclerView.adapter = adapter

        showOrders(userId)

    }


    private fun showOrders(userId: String) {
        db.collection("orders")
            .get()
            .addOnSuccessListener { task ->
                items.clear()
                for (document in task) {
                    val orderId = document.get("order_id").toString()

                    if(orderId.contains(userId)){

                        val time = orderId.split("_")[1]
                        val t : Long = time.toLong()
                        val orderStatus = if (document.getBoolean("is_active") == true) {
                            "Pending"
                        } else {
                            "Completed"
                        }
                        val qr_url = document.get("qr_url").toString()

                        items.add(MyOrderItem(orderId, orderStatus, t, qr_url))

                    }

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
