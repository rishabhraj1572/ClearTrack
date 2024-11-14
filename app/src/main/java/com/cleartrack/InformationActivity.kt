package com.cleartrack

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log

class InformationActivity : AppCompatActivity() {

    private lateinit var logisticTextView : TextView
    private lateinit var locationTextView : TextView
    private lateinit var pincodeTextView : TextView
    private lateinit var timeTextView : TextView
    private lateinit var emailTextView : TextView
    private lateinit var phoneTextView : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        logisticTextView=findViewById<TextView>(R.id.logisticTextView)
        locationTextView=findViewById<TextView>(R.id.locationTextView)
        pincodeTextView=findViewById<TextView>(R.id.pincodeTextView)
        timeTextView=findViewById<TextView>(R.id.timeTextView)
        emailTextView=findViewById<TextView>(R.id.emailTextView)
        phoneTextView=findViewById<TextView>(R.id.phoneTextView)


        val location = intent.getStringExtra("location")
        val logistics = intent.getStringExtra("logistics")
        val pincode = intent.getStringExtra("pincode")
        val time = intent.getStringExtra("time")

        logisticTextView.text = logistics ?: "N/A"
        locationTextView.text = location ?: "N/A"
        pincodeTextView.text = pincode ?: "N/A"
        timeTextView.text = time ?: "N/A"
        emailTextView.text = "null"  //for now  :)
        phoneTextView.text = "null"






    }

}