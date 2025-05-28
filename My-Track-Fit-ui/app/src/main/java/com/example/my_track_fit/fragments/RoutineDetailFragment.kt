package com.example.my_track_fit.fragments

import android.app.AlertDialog // Para mostrar diálogos de alerta
import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.Button // Botón de UI
import android.widget.EditText // Campo de texto editable
import android.widget.TextView // Vista para mostrar texto
import android.widget.ImageButton // Botón de imagen para volver
import androidx.fragment.app.Fragment // Clase base para fragmentos
import androidx.recyclerview.widget.LinearLayoutManager // LayoutManager para listas verticales
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.model.Routine // Modelo de rutina
import android.widget.Toast // Para mostrar mensajes cortos al usuario
import android.widget.Spinner // Selector desplegable
import android.widget.ArrayAdapter // Adaptador para el spinner
import com.example.my_track_fit.MainActivity // Actividad principal
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.adapters.BlockAdapter // Adaptador para bloques

/**
 * Fragmento que muestra el detalle de una rutina, incluyendo su nombre,
 * las semanas, los bloques y opciones para agregar o eliminar semanas/bloques.
 * Incluye un botón de volver en la esquina superior derecha.
 */
class RoutineDetailFragment : Fragment() {
    companion object {
        private const val ARG_ROUTINE_INDEX = "routine_index" // Constante para el argumento del índice de rutina
        fun newInstance(routineIndex: Int): RoutineDetailFragment {
            val fragment = RoutineDetailFragment()
            val args = Bundle()
            args.putInt(ARG_ROUTINE_INDEX, routineIndex) // Guarda el índice de rutina en el bundle
            fragment.arguments = args
            return fragment
        }
    }

    private var routine: Routine? = null // Referencia a la rutina actual
    private lateinit var blockAdapter: BlockAdapter // Adaptador para los bloques
    private var routineIndex: Int = -1 // Índice de la rutina seleccionada
    private lateinit var spinnerWeeks: Spinner // Spinner para seleccionar la semana
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar los bloques
    private lateinit var btnAddWeek: Button // Botón para agregar semana
    private lateinit var btnAddBlock: Button // Botón para agregar bloque

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

    // Callback para long click en un bloque (cambiar nombre o eliminar)
    private val onBlockLongClick: (com.example.my_track_fit.model.Block, Int) -> Unit = { block, position ->
        val selectedWeekIndex = spinnerWeeks.selectedItemPosition // Índice de la semana seleccionada
        val week = routine?.getWeeks()?.getOrNull(selectedWeekIndex) // Obtiene la semana actual
        val context = requireContext()
        val options = arrayOf("Cambiar nombre", "Eliminar bloque") // Opciones para el diálogo
        AlertDialog.Builder(context)
            .setTitle("Opciones de bloque")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Cambiar nombre
                        val editText = EditText(context)
                        editText.setText(block.getName())
                        AlertDialog.Builder(context)
                            .setTitle("Cambiar nombre del bloque")
                            .setView(editText)
                            .setPositiveButton("Aceptar") { _, _ ->
                                val newName = editText.text.toString().trim()
                                if (newName.isNotEmpty()) {
                                    block.setName(newName) // Cambia el nombre del bloque
                                    updateBlocksForSelectedWeek() // Actualiza la lista de bloques
                                    // Guardar rutinas en archivo local después de modificar el nombre del bloque
                                    val workout = (activity as? MainActivity)?.workout
                                    val rutinas = workout?.getRoutines() ?: listOf()
                                    val gson = com.google.gson.Gson()
                                    val json = gson.toJson(rutinas)
                                    saveToFile("rutinas.json", json)
                                } else {
                                    Toast.makeText(context, "Debe escribir al menos un caracter", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                    1 -> { // Eliminar bloque
                        AlertDialog.Builder(context)
                            .setTitle("Eliminar bloque")
                            .setMessage("¿Realmente quieres borrar el bloque \"${block.getName()}\"?")
                            .setPositiveButton("Aceptar") { _, _ ->
                                week?.deleteBlock(block) // Elimina el bloque de la semana
                                val weeks = routine?.getWeeks()
                                if (week != null && week.getBlockList().isEmpty()) {
                                    if (weeks != null && weeks.size > 1) {
                                        weeks.removeAt(selectedWeekIndex) // Elimina la semana si queda vacía y hay más de una
                                        Toast.makeText(context, "Semana eliminada porque no tiene bloques", Toast.LENGTH_SHORT).show()
                                        updateWeeksSpinnerAndButton((selectedWeekIndex - 1).coerceAtLeast(0))
                                    } else {
                                        // Si es la única semana, solo actualiza la vista
                                        updateBlocksForSelectedWeek()
                                    }
                                } else {
                                    updateBlocksForSelectedWeek()
                                }
                                // Si solo queda una semana y está vacía, oculta el botón "+ semana"
                                if (weeks != null && weeks.size == 1 && weeks[0].getBlockList().isEmpty()) {
                                    btnAddWeek.visibility = View.GONE
                                } else {
                                    btnAddWeek.visibility = View.VISIBLE
                                }
                                // Guardar rutinas en archivo local después de eliminar el bloque
                                val workout = (activity as? MainActivity)?.workout
                                val rutinas = workout?.getRoutines() ?: listOf()
                                val gson = com.google.gson.Gson()
                                val json = gson.toJson(rutinas)
                                saveToFile("rutinas.json", json)
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }
            }
            .show()
    }

    // Actualiza la lista de bloques para la semana seleccionada
    private fun updateBlocksForSelectedWeek() {
        val selectedWeekIndex = spinnerWeeks.selectedItemPosition // Índice de la semana seleccionada
        val week = routine?.getWeeks()?.getOrNull(selectedWeekIndex) // Obtiene la semana actual
        blockAdapter = BlockAdapter(
            week?.getBlockList() ?: listOf(),
            routineIndex,
            selectedWeekIndex,
            onBlockLongClick
        )
        recyclerView.adapter = blockAdapter // Asigna el adapter actualizado al RecyclerView
    }

    // Actualiza el spinner de semanas y el botón de agregar semana
    private fun updateWeeksSpinnerAndButton(selectedIndex: Int = spinnerWeeks.selectedItemPosition) {
        val weeks = routine?.getWeeks() ?: mutableListOf()
        val weekNames = weeks.mapIndexed { index, _ -> "Semana ${index + 1}" } // Nombres de las semanas
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeeks.adapter = adapter

        btnAddWeek.visibility = if (
            weeks.size == 1 && weeks[0].getBlockList().isEmpty()
        ) View.GONE else View.VISIBLE

        // Selecciona la semana indicada (o la última si el índice es inválido)
        if (weeks.isNotEmpty()) {
            spinnerWeeks.setSelection(selectedIndex.coerceAtMost(weeks.size - 1))
        }
    }

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_routine_detail, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routineIndex = arguments?.getInt(ARG_ROUTINE_INDEX) ?: -1 // Obtiene el índice de rutina desde los argumentos
        val workout = (activity as? MainActivity)?.workout // Obtiene el workout desde la actividad principal
        routine = workout?.getRoutines()?.getOrNull(routineIndex) // Obtiene la rutina correspondiente

        val tvRoutineName = view.findViewById<TextView>(R.id.tvRoutineName) // Referencia al TextView del nombre de la rutina
        recyclerView = view.findViewById(R.id.blocksRecyclerView) // RecyclerView para los bloques
        spinnerWeeks = view.findViewById(R.id.spinnerWeeks) // Spinner para seleccionar la semana
        btnAddWeek = view.findViewById(R.id.btnAddWeek) // Botón para agregar semana
        btnAddBlock = view.findViewById(R.id.btnAddBlock) // Botón para agregar bloque
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack) // Botón para volver atrás

        // Configura el botón de volver para regresar al menú anterior
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        tvRoutineName.text = routine?.getName() ?: "" // Muestra el nombre de la rutina

        // Inicializar el RecyclerView y el adapter con los bloques de la primera semana (o lista vacía)
        val initialWeekIndex = 0
        val initialBlocks = routine?.getWeeks()?.firstOrNull()?.getBlockList() ?: listOf()
        blockAdapter = BlockAdapter(
            initialBlocks,
            routineIndex,
            initialWeekIndex
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Layout vertical para la lista
        recyclerView.adapter = blockAdapter // Asigna el adapter al RecyclerView

        updateWeeksSpinnerAndButton() // Actualiza el spinner de semanas y el botón

        // Listener para cambio de selección en el spinner de semanas
        spinnerWeeks.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                updateBlocksForSelectedWeek() // Actualiza los bloques al cambiar de semana
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        // Listener para el botón de agregar semana
        btnAddWeek.setOnClickListener {
            val options = arrayOf("Copiar semana completa", "Copiar ejercicios de la semana") // Opciones para el diálogo
            AlertDialog.Builder(requireContext())
                .setTitle("Agregar semana")
                .setItems(options) { dialog, which ->
                    val selectedWeekIndex = spinnerWeeks.selectedItemPosition
                    val selectedWeek = routine?.getWeeks()?.getOrNull(selectedWeekIndex)
                    if (selectedWeek != null) {
                        val newWeek = when (which) {
                            0 -> routine?.copyWeekData(selectedWeek) // Copia semana completa
                            1 -> routine?.copyWeekNoData(selectedWeek) // Copia solo ejercicios
                            else -> null
                        }
                        if (newWeek != null) {
                            routine?.getWeeks()?.add(newWeek) // Agrega la nueva semana
                            updateWeeksSpinnerAndButton((routine?.getWeeks()?.size ?: 1) - 1)
                            spinnerWeeks.setSelection((routine?.getWeeks()?.size ?: 1) - 1)
                            // Guardar rutinas en archivo local después de agregar una semana
                            val workout = (activity as? MainActivity)?.workout
                            val rutinas = workout?.getRoutines() ?: listOf()
                            val gson = com.google.gson.Gson()
                            val json = gson.toJson(rutinas)
                            saveToFile("rutinas.json", json)
                        }
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Listener para el botón de agregar bloque
        btnAddBlock.setOnClickListener {
            val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
            val editText = EditText(requireContext())
            editText.hint = "Nombre del bloque"

            AlertDialog.Builder(requireContext())
                .setTitle("Agregar bloque")
                .setView(editText)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val blockName = editText.text.toString().trim()
                    val selectedWeekIndex = spinnerWeeks.selectedItemPosition
                    val week = routine?.getWeeks()?.getOrNull(selectedWeekIndex)
                    if (blockName.isNotEmpty() && week != null) {
                        week.addBlock(blockName) // Agrega el bloque a la semana
                        updateBlocksForSelectedWeek() // Actualiza la lista de bloques
                        updateWeeksSpinnerAndButton(spinnerWeeks.selectedItemPosition) // Actualiza el spinner
                        Toast.makeText(requireContext(), "Bloque agregado", Toast.LENGTH_SHORT).show()
                        // Guardar rutinas en archivo local después de agregar un bloque
                        val workout = (activity as? MainActivity)?.workout
                        val rutinas = workout?.getRoutines() ?: listOf()
                        val gson = com.google.gson.Gson()
                        val json = gson.toJson(rutinas)
                        saveToFile("rutinas.json", json)
                        // Si no puedes acceder a saveToFile, crea una función local similar aquí
                    } else {
                        Toast.makeText(requireContext(), "Debes ingresar un nombre", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}