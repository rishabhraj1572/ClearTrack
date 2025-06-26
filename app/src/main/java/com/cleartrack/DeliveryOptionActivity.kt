package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DeliveryOptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_option)

        val submit=findViewById<Button>(R.id.btnSubmit)
        val orderId = intent.getStringExtra("orderID")
        submit.setOnClickListener{
            val intent = Intent(this,QRGenerateActivity::class.java)
            intent.putExtra("orderId",orderId)
            startActivity(intent)
        }
    }
}