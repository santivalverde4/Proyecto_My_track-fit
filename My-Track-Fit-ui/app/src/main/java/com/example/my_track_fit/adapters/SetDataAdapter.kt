package com.example.my_track_fit.adapters

import android.text.Editable // Permite manipular texto editable en EditText
import android.text.TextWatcher // Permite escuchar cambios en EditText
import android.view.LayoutInflater // Permite inflar layouts XML a vistas
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.EditText // Campo de texto editable
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.model.ExerciseInstance // Modelo de instancia de ejercicio
import android.widget.ImageButton // Botón con imagen
import com.example.my_track_fit.MainActivity // Actividad principal
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)

class SetDataAdapter(
    private val setsData: MutableList<ExerciseInstance.SetData>, // Lista de datos de sets a mostrar
    private val onDeleteSet: ((Int) -> Unit)? = null // Callback para eliminar un set
) : RecyclerView.Adapter<SetDataAdapter.SetDataViewHolder>() {

    // ViewHolder que contiene la vista de cada set
    class SetDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etWeight: EditText = itemView.findViewById(R.id.etWeight) // Campo para el peso
        val etReps: EditText = itemView.findViewById(R.id.etReps) // Campo para las repeticiones
        val etRpe: EditText = itemView.findViewById(R.id.etRpe) // Campo para el RPE
        val btnDeleteSet: ImageButton? = itemView.findViewById(R.id.btnDeleteSet) // Botón para eliminar el set
    }

    // Crea nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_set_data, parent, false) // Infla el layout de cada set
        return SetDataViewHolder(view) // Devuelve un nuevo ViewHolder
    }

    // Asigna los datos de un set a la vista correspondiente
    override fun onBindViewHolder(holder: SetDataViewHolder, position: Int) {
        val setData = setsData[position] // Obtiene los datos del set en la posición dada

        // Establece el texto de los campos con los valores actuales del set
        holder.etWeight.setText(setData.weight.toString())
        holder.etReps.setText(setData.reps.toString())
        holder.etRpe.setText(setData.rpe.toString())

        // Listener para cambios en el campo de peso
        holder.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setData.weight = s?.toString()?.toIntOrNull() ?: 0 // Actualiza el peso
                guardarRutinasEnArchivo(holder.itemView.context) // Guarda cambios en archivo
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Listener para cambios en el campo de repeticiones
        holder.etReps.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setData.reps = s?.toString()?.toIntOrNull() ?: 0 // Actualiza las repeticiones
                guardarRutinasEnArchivo(holder.itemView.context) // Guarda cambios en archivo
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Listener para cambios en el campo de RPE
        holder.etRpe.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setData.rpe = s?.toString()?.toIntOrNull() ?: 0 // Actualiza el RPE
                guardarRutinasEnArchivo(holder.itemView.context) // Guarda cambios en archivo
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener para el botón de eliminar set
        holder.btnDeleteSet?.setOnClickListener {
            val pos = holder.adapterPosition // Obtiene la posición actual del ViewHolder
            if (pos != RecyclerView.NO_POSITION && pos >= 0 && pos < setsData.size) {
                onDeleteSet?.invoke(pos) // Llama al callback para eliminar el set
            }
            // Si el índice no es válido, simplemente no hace nada
        }
    }

    // Devuelve la cantidad de sets en la lista
    override fun getItemCount(): Int = setsData.size

    // Guarda las rutinas en un archivo JSON local
    private fun guardarRutinasEnArchivo(context: android.content.Context) {
        val workout = (context as? MainActivity)?.workout // Obtiene el workout desde la actividad principal
        val rutinas = workout?.getRoutines() ?: listOf() // Obtiene la lista de rutinas
        val gson = com.google.gson.Gson() // Instancia de Gson para serializar
        val json = gson.toJson(rutinas) // Convierte la lista de rutinas a JSON
        context.openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
            it.write(json.toByteArray()) // Escribe el JSON en el archivo
        }
    }
}