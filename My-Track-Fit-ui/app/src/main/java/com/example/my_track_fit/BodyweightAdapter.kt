// Nuevo archivo: BodyweightAdapter.kt
package com.example.my_track_fit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.network.Bodyweight
import com.example.my_track_fit.UserSession

class BodyweightAdapter(
    private val onItemClick: (Bodyweight) -> Unit
) : RecyclerView.Adapter<BodyweightAdapter.BodyweightViewHolder>() {

    private var items: List<Bodyweight> = emptyList()

    fun submitList(list: List<Bodyweight>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyweightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bodyweight, parent, false)
        return BodyweightViewHolder(view)
    }

    override fun onBindViewHolder(holder: BodyweightViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    class BodyweightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pesoText: TextView = itemView.findViewById(R.id.txtPeso)
        private val fechaText: TextView = itemView.findViewById(R.id.txtFecha)
        fun bind(item: Bodyweight) {
            pesoText.text = "${item.peso} kg"
            fechaText.text = item.fecha
        }
    }
}