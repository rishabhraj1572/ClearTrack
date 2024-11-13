package com.cleartrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val location = intent.getStringExtra("location")
        val logisctics = intent.getStringExtra("logistics")
        val pincode = intent.getStringExtra("pincode")
        val time = intent.getStringExtra("time")


    }

}