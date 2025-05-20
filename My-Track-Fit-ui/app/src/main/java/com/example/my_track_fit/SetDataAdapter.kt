package com.example.my_track_fit

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.ExerciseInstance

class SetDataAdapter(
    private val setsData: MutableList<ExerciseInstance.SetData>,
    private val onDeleteSet: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<SetDataAdapter.SetDataViewHolder>() {

    class SetDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etWeight: EditText = itemView.findViewById(R.id.etWeight)
        val etReps: EditText = itemView.findViewById(R.id.etReps)
        val etRpe: EditText = itemView.findViewById(R.id.etRpe)
        val btnDeleteSet: Button? = itemView.findViewById(R.id.btnDeleteSet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_set_data, parent, false)
        return SetDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetDataViewHolder, position: Int) {
        val setData = setsData[position]

        // Evita loops de TextWatcher al reciclar vistas
        holder.etWeight.setText(setData.weight.toString())
        holder.etReps.setText(setData.reps.toString())
        holder.etRpe.setText(setData.rpe.toString())

        holder.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setData.weight = s?.toString()?.toIntOrNull() ?: 0
                guardarRutinasEnArchivo(holder.itemView.context)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        holder.etReps.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setData.reps = s?.toString()?.toIntOrNull() ?: 0
                guardarRutinasEnArchivo(holder.itemView.context)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        holder.etRpe.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setData.rpe = s?.toString()?.toIntOrNull() ?: 0
                guardarRutinasEnArchivo(holder.itemView.context)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.btnDeleteSet?.setOnClickListener {
            onDeleteSet?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = setsData.size

    private fun guardarRutinasEnArchivo(context: android.content.Context) {
    val workout = (context as? MainActivity)?.workout
    val rutinas = workout?.getRoutines() ?: listOf()
    val gson = com.google.gson.Gson()
    val json = gson.toJson(rutinas)
    context.openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
        it.write(json.toByteArray())
    }
}
}