package com.example.my_track_fit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.ExerciseInstance

class ExerciseInstanceAdapter(
    private val exerciseInstances: MutableList<ExerciseInstance>,
    private val onExerciseInstanceLongClick: (ExerciseInstance, Int) -> Unit,
    private val onExerciseInstanceClick: (ExerciseInstance) -> Unit
) : RecyclerView.Adapter<ExerciseInstanceAdapter.ExerciseInstanceViewHolder>() {

    class ExerciseInstanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseInstanceName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseInstanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_instance, parent, false)
        return ExerciseInstanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseInstanceViewHolder, position: Int) {
        val instance = exerciseInstances[position]
        holder.tvExerciseName.text = instance.getExercise().getName()
        holder.itemView.setOnLongClickListener {
            onExerciseInstanceLongClick(instance, position)
            true
        }
        holder.itemView.setOnClickListener {
            onExerciseInstanceClick(instance)
        }
    }

    override fun getItemCount(): Int = exerciseInstances.size
}