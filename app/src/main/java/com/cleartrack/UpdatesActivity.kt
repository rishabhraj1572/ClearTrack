package com.cleartrack

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UpdatesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val updateBtn : Button = findViewById(R.id.update)



        val items = listOf(
            UpdateItem("Text11", "Text12", "Text13", "Text14"),
            UpdateItem("Text21", "Text22", "Text23", "Text24"),
            UpdateItem("Text31", "Text32", "Text33", "Text34"),
            UpdateItem("Text41", "Text42", "Text43", "Text44")
        )

        // Set the adapter
        val adapter = UpdatesAdapter(items)
        recyclerView.adapter = adapter
    }
}
