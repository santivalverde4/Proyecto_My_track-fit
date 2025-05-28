package com.example.my_track_fit.fragments

import android.app.AlertDialog // Para mostrar diálogos de alerta
import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.text.InputType // Para definir el tipo de entrada de texto
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.Button // Botón de UI
import android.widget.EditText // Campo de texto editable
import android.widget.TextView // Vista para mostrar texto
import android.widget.Toast // Para mostrar mensajes cortos al usuario
import androidx.fragment.app.Fragment // Clase base para fragmentos
import androidx.recyclerview.widget.LinearLayoutManager // LayoutManager para listas verticales
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.adapters.LocalDateAdapter // Adaptador para fechas LocalDate
import com.example.my_track_fit.model.BodyWeight // Modelo de BodyWeight
import com.example.my_track_fit.model.Mark // Modelo de marca de peso
import com.google.gson.GsonBuilder // Para serializar/deserializar JSON
import java.time.LocalDate // Clase para fechas sin tiempo

class BodyweightFragment : Fragment() {

    // Guarda un string en un archivo local
    private fun saveToFile(filename: String, data: String) {
        requireContext().openFileOutput(filename, android.content.Context.MODE_PRIVATE).use {
            it.write(data.toByteArray()) // Escribe los datos en el archivo
        }
    }

    // Lee el contenido de un archivo local como string
    private fun readFromFile(filename: String): String {
        return requireContext().openFileInput(filename).bufferedReader().use { it.readText() } // Lee todo el archivo como texto
    }

    private lateinit var bodyWeight: BodyWeight // Instancia del modelo BodyWeight
    private lateinit var adapter: MarkAdapter // Adaptador para la lista de marcas

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bodyweight, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar marcas desde archivo local (bodyweight.json)
        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate::class.java, LocalDateAdapter()) // Usa el adaptador para fechas
            .create()
        val marksList: MutableList<Mark> = try {
            val json = readFromFile("bodyweight.json") // Lee el archivo de marcas
            val type = com.google.gson.reflect.TypeToken.getParameterized(MutableList::class.java, Mark::class.java).type // Tipo para deserializar la lista
            gson.fromJson(json, type) ?: mutableListOf() // Deserializa o crea lista vacía
        } catch (e: Exception) {
            mutableListOf() // Si hay error, crea lista vacía
        }

        bodyWeight = BodyWeight(marksList) // Inicializa el modelo con la lista de marcas

        // Título
        val tvTitle = view.findViewById<TextView>(R.id.tvBodyWeightTitle) // Referencia al TextView del título
        tvTitle.text = "Marcas de peso" // Establece el texto del título

        // Botón "+"
        val btnAddMark = view.findViewById<Button>(R.id.btnAddMark) // Referencia al botón para agregar marca

        // RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerBodyWeightMarks) // Referencia al RecyclerView
        adapter = MarkAdapter(bodyWeight.getBodyWeightMarks(),
            onMarkLongClick = { mark, position ->
                val options = arrayOf("Eliminar marca", "Modificar marca") // Opciones para el diálogo
                AlertDialog.Builder(requireContext())
                    .setTitle("Opciones de marca")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> { // Eliminar marca
                                bodyWeight.deleteBodyWeightMark(mark) // Elimina la marca del modelo
                                adapter.notifyItemRemoved(position) // Notifica al adapter
                                // Guardar después de eliminar
                                val gson = GsonBuilder()
                                    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                                    .create()
                                val json = gson.toJson(bodyWeight.getBodyWeightMarks()) // Serializa la lista
                                saveToFile("bodyweight.json", json) // Guarda en archivo
                            }
                            1 -> { // Modificar marca
                                val inputView = EditText(requireContext()) // Campo de texto para el nuevo peso
                                inputView.hint = "Nuevo peso (kg)"
                                inputView.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                                inputView.setText(mark.getBodyWeightMark().toString()) // Muestra el peso actual
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Modificar marca de peso")
                                    .setView(inputView)
                                    .setPositiveButton("Guardar") { _, _ ->
                                        val pesoStr = inputView.text.toString().trim() // Obtiene el texto ingresado
                                        val peso = pesoStr.toDoubleOrNull() // Convierte a Double
                                        if (peso != null) {
                                            mark.setBodyWeightMark(peso) // Actualiza el peso
                                            mark.setActualDate() // Actualiza la fecha
                                            adapter.notifyItemChanged(position) // Notifica al adapter
                                            // Guardar después de modificar
                                            val gson = GsonBuilder()
                                                .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                                                .create()
                                            val json = gson.toJson(bodyWeight.getBodyWeightMarks())
                                            saveToFile("bodyweight.json", json)
                                        } else {
                                            Toast.makeText(requireContext(), "Ingresa un peso válido", Toast.LENGTH_SHORT).show() // Muestra error si el peso no es válido
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Layout vertical para la lista
        recyclerView.adapter = adapter // Asigna el adapter al RecyclerView

        // Funcionalidad del botón "+"
        btnAddMark.setOnClickListener {
            val inputView = android.widget.EditText(requireContext()) // Campo de texto para el nuevo peso
            inputView.hint = "Peso actual (kg)"
            inputView.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Agregar marca de peso")
                .setView(inputView)
                .setPositiveButton("Agregar") { _, _ ->
                    val pesoStr = inputView.text.toString().trim() // Obtiene el texto ingresado
                    val peso = pesoStr.toDoubleOrNull() // Convierte a Double
                    if (peso != null) {
                        bodyWeight.addBodyWeightMark(peso) // Agrega la marca al modelo
                        adapter.notifyItemInserted(bodyWeight.getBodyWeightMarks().size - 1) // Notifica al adapter
                        // Guardar marcas en archivo local después de agregar una marca
                        val gson = com.google.gson.GsonBuilder()
                            .registerTypeAdapter(java.time.LocalDate::class.java, LocalDateAdapter())
                            .create()
                        val json = gson.toJson(bodyWeight.getBodyWeightMarks())
                        saveToFile("bodyweight.json", json)
                    } else {
                        android.widget.Toast.makeText(requireContext(), "Ingresa un peso válido", android.widget.Toast.LENGTH_SHORT).show() // Muestra error si el peso no es válido
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}

// Adapter para mostrar las marcas de peso
class MarkAdapter(
    private val marks: MutableList<Mark>, // Lista de marcas de peso
    private val onMarkLongClick: ((Mark, Int) -> Unit)? = null // Callback para long click en una marca
) : RecyclerView.Adapter<MarkAdapter.MarkViewHolder>() {
    class MarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMark: TextView = itemView.findViewById(android.R.id.text1) // Referencia al TextView de la marca
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) // Infla el layout simple para cada marca
        return MarkViewHolder(view) // Devuelve un nuevo ViewHolder
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        val mark = marks[position] // Obtiene la marca en la posición dada
        holder.tvMark.text = "${mark.getBodyWeightMark()} kg - ${mark.getDate()}" // Muestra el peso y la fecha
        holder.itemView.setOnLongClickListener {
            onMarkLongClick?.invoke(mark, position) // Ejecuta el callback si existe
            true // Indica que el evento fue consumido
        }
    }

    override fun getItemCount(): Int = marks.size // Devuelve la cantidad de marcas en la lista
}