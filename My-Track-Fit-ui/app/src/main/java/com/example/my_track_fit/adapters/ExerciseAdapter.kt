package com.example.my_track_fit.adapters

import android.view.LayoutInflater // Permite inflar layouts XML a vistas
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.TextView // Vista para mostrar texto
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.model.Exercise // Modelo de ejercicio

// Adaptador para mostrar una lista de ejercicios en un RecyclerView
class ExerciseAdapter(
    private val exercises: List<Exercise>, // Lista de ejercicios a mostrar
    private val onExerciseLongClick: (Exercise, Int) -> Unit // Callback para long click en un ejercicio
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // ViewHolder que contiene la vista de cada ejercicio
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName) // Referencia al TextView del nombre del ejercicio
    }

    // Crea nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false) // Infla el layout de cada item de ejercicio
        return ExerciseViewHolder(view) // Devuelve un nuevo ViewHolder
    }

    // Asigna los datos de un ejercicio a la vista correspondiente
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.tvExerciseName.text = exercises[position].getName() // Muestra el nombre del ejercicio

        // Listener para long click: ejecuta el callback con el ejercicio y la posición
        holder.itemView.setOnLongClickListener {
            onExerciseLongClick(exercises[position], position)
            true // Indica que el evento fue consumido
        }
    }

    // Devuelve la cantidad de elementos en la lista
    override fun getItemCount(): Int = exercises.size
}