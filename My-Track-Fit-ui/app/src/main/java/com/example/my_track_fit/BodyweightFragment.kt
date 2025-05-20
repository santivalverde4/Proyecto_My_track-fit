package com.example.my_track_fit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.BodyWeight
import com.example.my_track_fit.model.Mark

class BodyweightFragment : Fragment() {

    // Guarda un string en un archivo local
    private fun saveToFile(filename: String, data: String) {
        requireContext().openFileOutput(filename, android.content.Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    // Lee el contenido de un archivo local como string
    private fun readFromFile(filename: String): String {
        return requireContext().openFileInput(filename).bufferedReader().use { it.readText() }
    }

    private lateinit var bodyWeight: BodyWeight
    private lateinit var adapter: MarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bodyweight, container, false)
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar marcas desde archivo local (bodyweight.json)
        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate::class.java, com.example.my_track_fit.LocalDateAdapter())
            .create()
        val marksList: MutableList<Mark> = try {
            val json = readFromFile("bodyweight.json")
            val type = com.google.gson.reflect.TypeToken.getParameterized(MutableList::class.java, Mark::class.java).type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }

        bodyWeight = BodyWeight(marksList)

        // Título
        val tvTitle = view.findViewById<TextView>(R.id.tvBodyWeightTitle)
        tvTitle.text = "Marcas de peso"

        // Botón "+"
        val btnAddMark = view.findViewById<Button>(R.id.btnAddMark)

        // RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerBodyWeightMarks)
        adapter = MarkAdapter(bodyWeight.getBodyWeightMarks(),
            onMarkLongClick = { mark, position ->
                val options = arrayOf("Eliminar marca", "Modificar marca")
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Opciones de marca")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> { // Eliminar marca
                                bodyWeight.deleteBodyWeightMark(mark)
                                adapter.notifyItemRemoved(position)
                                // Guardar después de eliminar
                                val gson = com.google.gson.GsonBuilder()
                                    .registerTypeAdapter(java.time.LocalDate::class.java, com.example.my_track_fit.LocalDateAdapter())
                                    .create()
                                val json = gson.toJson(bodyWeight.getBodyWeightMarks())
                                saveToFile("bodyweight.json", json)
                            }
                            1 -> { // Modificar marca
                                val inputView = android.widget.EditText(requireContext())
                                inputView.hint = "Nuevo peso (kg)"
                                inputView.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                                inputView.setText(mark.getBodyWeightMark().toString())
                                android.app.AlertDialog.Builder(requireContext())
                                    .setTitle("Modificar marca de peso")
                                    .setView(inputView)
                                    .setPositiveButton("Guardar") { _, _ ->
                                        val pesoStr = inputView.text.toString().trim()
                                        val peso = pesoStr.toDoubleOrNull()
                                        if (peso != null) {
                                            mark.setBodyWeightMark(peso)
                                            mark.setActualDate()
                                            adapter.notifyItemChanged(position)
                                            // Guardar después de modificar
                                            val gson = com.google.gson.GsonBuilder()
                                                .registerTypeAdapter(java.time.LocalDate::class.java, com.example.my_track_fit.LocalDateAdapter())
                                                .create()
                                            val json = gson.toJson(bodyWeight.getBodyWeightMarks())
                                            saveToFile("bodyweight.json", json)
                                        } else {
                                            android.widget.Toast.makeText(requireContext(), "Ingresa un peso válido", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        }
                    }
                    .show()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Funcionalidad del botón "+"
        btnAddMark.setOnClickListener {
            val inputView = android.widget.EditText(requireContext())
            inputView.hint = "Peso actual (kg)"
            inputView.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Agregar marca de peso")
                .setView(inputView)
                .setPositiveButton("Agregar") { _, _ ->
                    val pesoStr = inputView.text.toString().trim()
                    val peso = pesoStr.toDoubleOrNull()
                    if (peso != null) {
                        bodyWeight.addBodyWeightMark(peso)
                        adapter.notifyItemInserted(bodyWeight.getBodyWeightMarks().size - 1)
                        // Guardar marcas en archivo local después de agregar una marca
                        val gson = com.google.gson.GsonBuilder()
                            .registerTypeAdapter(java.time.LocalDate::class.java, com.example.my_track_fit.LocalDateAdapter())
                            .create()
                        val json = gson.toJson(bodyWeight.getBodyWeightMarks())
                        saveToFile("bodyweight.json", json)
                    } else {
                        android.widget.Toast.makeText(requireContext(), "Ingresa un peso válido", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}


// Adapter para mostrar las marcas de peso
class MarkAdapter(
    private val marks: MutableList<Mark>,
    private val onMarkLongClick: ((Mark, Int) -> Unit)? = null
) : RecyclerView.Adapter<MarkAdapter.MarkViewHolder>() {
    class MarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMark: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        val mark = marks[position]
        holder.tvMark.text = "${mark.getBodyWeightMark()} kg - ${mark.getDate()}"
        holder.itemView.setOnLongClickListener {
            onMarkLongClick?.invoke(mark, position)
            true
        }
    }

    override fun getItemCount(): Int = marks.size
}