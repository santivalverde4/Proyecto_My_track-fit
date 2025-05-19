package com.example.my_track_fit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.Routine

class RoutineAdapter(
    private val routines: List<Routine>,
    private val onRoutineLongClick: (Routine, Int) -> Unit,
    private val onRoutineClick: (Routine, Int) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoutineName: TextView = itemView.findViewById(R.id.tvRoutineName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.tvRoutineName.text = routine.getName()
        holder.itemView.setOnLongClickListener {
            onRoutineLongClick(routine, position)
            true
        }
        holder.itemView.setOnClickListener {
            onRoutineClick(routine, position)
        }
    }

    override fun getItemCount(): Int = routines.size
}