package com.example.my_track_fit.adapters

import android.view.LayoutInflater // Permite inflar layouts XML a vistas
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.TextView // Vista para mostrar texto
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.model.ExerciseInstance // Modelo de instancia de ejercicio

// Adaptador para mostrar una lista de ExerciseInstance en un RecyclerView
class ExerciseInstanceAdapter(
    private val exerciseInstances: MutableList<ExerciseInstance>, // Lista de instancias de ejercicio a mostrar
    private val onExerciseInstanceLongClick: (ExerciseInstance, Int) -> Unit, // Callback para long click en un item
    private val onExerciseInstanceClick: (ExerciseInstance) -> Unit // Callback para click normal en un item
) : RecyclerView.Adapter<ExerciseInstanceAdapter.ExerciseInstanceViewHolder>() {

    // ViewHolder que contiene la vista de cada elemento de la lista
    class ExerciseInstanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseInstanceName) // Referencia al TextView del nombre del ejercicio
    }

    // Crea nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseInstanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_instance, parent, false) // Infla el layout de cada item
        return ExerciseInstanceViewHolder(view) // Devuelve un nuevo ViewHolder
    }

    // Asigna los datos de una instancia de ejercicio a la vista correspondiente
    override fun onBindViewHolder(holder: ExerciseInstanceViewHolder, position: Int) {
        val instance = exerciseInstances[position] // Obtiene la instancia de ejercicio en la posición dada
        holder.tvExerciseName.text = instance.getExercise().getName() // Muestra el nombre del ejercicio

        // Listener para long click: ejecuta el callback con la instancia y la posición
        holder.itemView.setOnLongClickListener {
            onExerciseInstanceLongClick(instance, position)
            true // Indica que el evento fue consumido
        }

        // Listener para click normal: ejecuta el callback con la instancia
        holder.itemView.setOnClickListener {
            onExerciseInstanceClick(instance)
        }
    }

    // Devuelve la cantidad de elementos en la lista
    override fun getItemCount(): Int = exerciseInstances.size
}