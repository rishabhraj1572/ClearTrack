package com.cleartrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UpdatesAdapter(
    private val items: List<UpdateItem>
) : RecyclerView.Adapter<UpdatesAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView1: TextView = view.findViewById(R.id.locationTextView)
        val textView2: TextView = view.findViewById(R.id.timeTextView)
        val textView3: TextView = view.findViewById(R.id.pincodeTextView)
        val textView4: TextView = view.findViewById(R.id.logisticTextView)
        val textView5: TextView = view.findViewById(R.id.Status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.update_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textView1.text = item.location
        holder.textView2.text = item.text4
        holder.textView3.text = item.pincode
        holder.textView4.text = item.logistics
        holder.textView5.text = item.status

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
