package com.example.my_track_fit.adapters

import android.view.LayoutInflater // Permite inflar layouts XML a vistas
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.TextView // Vista para mostrar texto
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.model.Routine // Modelo de rutina

// Adaptador para mostrar una lista de rutinas en un RecyclerView
class RoutineAdapter(
    private val routines: List<Routine>, // Lista de rutinas a mostrar
    private val onRoutineLongClick: (Routine, Int) -> Unit, // Callback para long click en una rutina
    private val onRoutineClick: (Routine, Int) -> Unit // Callback para click normal en una rutina
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    // ViewHolder que contiene la vista de cada rutina
    class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoutineName: TextView = itemView.findViewById(R.id.tvRoutineName) // Referencia al TextView del nombre de la rutina
    }

    // Crea nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine, parent, false) // Infla el layout de cada item de rutina
        return RoutineViewHolder(view) // Devuelve un nuevo ViewHolder
    }

    // Asigna los datos de una rutina a la vista correspondiente
    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position] // Obtiene la rutina en la posición dada
        holder.tvRoutineName.text = routine.getName() // Muestra el nombre de la rutina

        // Listener para long click: ejecuta el callback con la rutina y la posición
        holder.itemView.setOnLongClickListener {
            onRoutineLongClick(routine, position)
            true // Indica que el evento fue consumido
        }

        // Listener para click normal: ejecuta el callback con la rutina y la posición
        holder.itemView.setOnClickListener {
            onRoutineClick(routine, position)
        }
    }

    // Devuelve la cantidad de elementos en la lista
    override fun getItemCount(): Int = routines.size
}