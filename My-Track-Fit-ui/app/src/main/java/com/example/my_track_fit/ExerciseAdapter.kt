package com.example.my_track_fit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.Exercise

class ExerciseAdapter(
    private var exercises: List<Exercise>,
    private var onExerciseLongClick: (Exercise, Int) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.tvExerciseName.text = exercises[position].getName()
        holder.itemView.setOnLongClickListener {
            onExerciseLongClick(exercises[position], position)
            true
        }
    }

    fun updateList(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    fun setOnExerciseLongClick(callback: (Exercise, Int) -> Unit) {
        onExerciseLongClick = callback
    }

    override fun getItemCount(): Int = exercises.size
}