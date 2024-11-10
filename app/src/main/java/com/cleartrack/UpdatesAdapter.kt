package com.cleartrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UpdatesAdapter(private val items: List<UpdateItem>) : RecyclerView.Adapter<UpdatesAdapter.ViewHolder>() {

    // ViewHolder class that holds references to the four TextViews
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView1: TextView = view.findViewById(R.id.locationTextView)
        val textView2: TextView = view.findViewById(R.id.timeTextView)
        val textView3: TextView = view.findViewById(R.id.pincodeTextView)
        val textView4: TextView = view.findViewById(R.id.logisticTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.update_item, parent, false) // Inflate the custom layout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data for the current item
        val item = items[position]

        // Bind data to the respective TextViews
        holder.textView1.text = item.text1
        holder.textView2.text = item.text2
        holder.textView3.text = item.text3
        holder.textView4.text = item.text4
    }

    override fun getItemCount(): Int = items.size
}
