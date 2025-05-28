package com.example.my_track_fit.fragments

import android.app.AlertDialog // Para mostrar diálogos de alerta
import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.Button // Botón de UI
import android.widget.EditText // Campo de texto editable
import androidx.fragment.app.Fragment // Clase base para fragmentos
import androidx.recyclerview.widget.LinearLayoutManager // LayoutManager para listas verticales
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import android.widget.Toast // Para mostrar mensajes cortos al usuario
import com.example.my_track_fit.MainActivity // Actividad principal
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.adapters.ExerciseAdapter // Adaptador para ejercicios
import com.example.my_track_fit.adapters.RoutineAdapter // Adaptador para rutinas
import com.google.gson.Gson // Para serializar/deserializar JSON

class WorkoutFragment : Fragment() {
    // Adapter para la lista de rutinas
    private lateinit var adapter: RoutineAdapter

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

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Acceder al workout de MainActivity
        val workout = (activity as? MainActivity)?.workout

        // Cargar ejercicios desde archivo local (ejercicios.json)
        try {
            val json = readFromFile("ejercicios.json")
            if (json.isNotEmpty()) {
                val gson = Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<com.example.my_track_fit.model.Exercise>>() {}.type
                val ejercicios: List<com.example.my_track_fit.model.Exercise> = gson.fromJson(json, type)
                workout?.setExercises(ejercicios) // Debes tener este método en tu modelo
            }
        } catch (e: Exception) {
            // El archivo no existe la primera vez, ignora el error
        }
        // Cargar rutinas desde archivo local (rutinas.json)
        try {
            val jsonRutinas = readFromFile("rutinas.json")
            if (jsonRutinas.isNotEmpty()) {
                val gson = Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<com.example.my_track_fit.model.Routine>>() {}.type
                val rutinas: List<com.example.my_track_fit.model.Routine> = gson.fromJson(jsonRutinas, type)
                workout?.setRoutines(rutinas.toMutableList())
            }
        } catch (e: Exception) {
            // El archivo no existe la primera vez, ignora el error
        }

        // Función para detectar el long click en una rutina
        val onRoutineLongClick: (com.example.my_track_fit.model.Routine, Int) -> Unit = { routine, position ->
            val options = arrayOf("Cambiar nombre", "Eliminar rutina") // Opciones para el diálogo
            AlertDialog.Builder(requireContext())
                .setTitle("Opciones de rutina")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> { // Cambiar nombre
                            val renameView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.dialog_rename_routine, null)
                            val etNewName = renameView.findViewById<EditText>(R.id.etRoutineName)
                            etNewName.setText(routine.getName())
                            AlertDialog.Builder(requireContext())
                                .setTitle("Cambiar nombre de rutina")
                                .setView(renameView)
                                .setPositiveButton("Aceptar") { _, _ ->
                                    val newName = etNewName.text.toString().trim()
                                    if (newName.isNotEmpty()) {
                                        routine.setName(newName) // Cambia el nombre de la rutina
                                        adapter.notifyItemChanged(position) // Notifica el cambio al adapter
                                        // Guardar rutinas en archivo local después de modificar el nombre
                                        val rutinas = workout?.getRoutines() ?: listOf()
                                        val gson = Gson()
                                        val json = gson.toJson(rutinas)
                                        saveToFile("rutinas.json", json)
                                    } else {
                                        Toast.makeText(requireContext(), "Debe escribir al menos un caracter", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                        1 -> { // Eliminar rutina
                            AlertDialog.Builder(requireContext())
                                .setTitle("Eliminar rutina")
                                .setMessage("¿Realmente quieres borrar la rutina \"${routine.getName()}\"?")
                                .setPositiveButton("Aceptar") { _, _ ->
                                    workout?.deleteRoutine(routine) // Elimina la rutina del modelo
                                    adapter.notifyDataSetChanged() // Notifica al adapter
                                    // Guardar rutinas en archivo local después de eliminar
                                    val rutinas = workout?.getRoutines() ?: listOf()
                                    val gson = Gson()
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

        // Inicializar el adapter con la lista de rutinas
        val routinesRecycler = view.findViewById<RecyclerView>(R.id.routinesListView) // RecyclerView para rutinas
        adapter = RoutineAdapter(
            workout?.getRoutines() ?: listOf(),
            onRoutineLongClick = onRoutineLongClick,
            onRoutineClick = { routine, position ->
                // Navegar al fragmento de detalle de la rutina seleccionada
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RoutineDetailFragment.newInstance(position))
                    .addToBackStack(null)
                    .commit()
            }
        )
        routinesRecycler.layoutManager = LinearLayoutManager(requireContext()) // Layout vertical para la lista
        routinesRecycler.adapter = adapter // Asigna el adapter al RecyclerView

        val addRoutineBtn = view.findViewById<View>(R.id.addRoutine) // Botón para agregar rutina
        addRoutineBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_routine, null)
            val etRoutineName = dialogView.findViewById<EditText>(R.id.etRoutineName)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss() // Cierra el diálogo al cancelar
            }
            dialogView.findViewById<Button>(R.id.btnAccept).setOnClickListener {
                // Manejar el nombre ingresado de la rutina
                val nombre = etRoutineName.text.toString()
                if (nombre.isEmpty()) {
                    // Notifica al usuario que debe escribir al menos un caracter
                    Toast.makeText(requireContext(), "Debe de escribir al menos un carácter!", Toast.LENGTH_SHORT).show()
                }
                else {
                    workout?.addRoutine(nombre) // Agrega la rutina al modelo
                    adapter.notifyDataSetChanged() // Notifica al adapter
                    // Guardar rutinas en archivo local
                    val rutinas = workout?.getRoutines() ?: listOf()
                    val gson = Gson()
                    val json = gson.toJson(rutinas)
                    saveToFile("rutinas.json", json)
                    dialog.dismiss() // Cierra el diálogo
                }
            }
            dialog.show()
        }

        // Botón para mostrar el diálogo de ejercicios
        val btnShowExercises = view.findViewById<Button>(R.id.btnShowExercises)
        btnShowExercises.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_exercise_list, null)
            val recyclerExercises = dialogView.findViewById<RecyclerView>(R.id.recyclerExercises)
            val btnAddExercise = dialogView.findViewById<Button>(R.id.btnAddExercise)

            // Callback para long click en Exercise
            val onExerciseLongClick: (com.example.my_track_fit.model.Exercise, Int) -> Unit = { exercise, position ->
                val options = arrayOf("Cambiar nombre", "Eliminar ejercicio") // Opciones para el diálogo
                AlertDialog.Builder(requireContext())
                    .setTitle("Opciones de ejercicio")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> { // Cambiar nombre
                                val inputView = EditText(requireContext())
                                inputView.setText(exercise.getName())
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Cambiar nombre del ejercicio")
                                    .setView(inputView)
                                    .setPositiveButton("Aceptar") { _, _ ->
                                        val newName = inputView.text.toString().trim()
                                        if (newName.isNotEmpty()) {
                                            exercise.setName(newName) // Cambia el nombre del ejercicio
                                            recyclerExercises.adapter?.notifyItemChanged(position) // Notifica al adapter
                                            // Guardar ejercicios en archivo local después de modificar el nombre
                                            val ejercicios = workout?.getExercise() ?: listOf()
                                            val gson = Gson()
                                            val json = gson.toJson(ejercicios)
                                            saveToFile("ejercicios.json", json)
                                        } else {
                                            Toast.makeText(requireContext(), "Debe escribir al menos un caracter", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                            1 -> { // Eliminar ejercicio
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Eliminar ejercicio")
                                    .setMessage("¿Realmente quieres borrar el ejercicio \"${exercise.getName()}\"?")
                                    .setPositiveButton("Aceptar") { _, _ ->
                                        workout?.deleteExercise(exercise) // Elimina el ejercicio del modelo
                                        recyclerExercises.adapter?.notifyDataSetChanged() // Notifica al adapter
                                        // Guardar ejercicios en archivo local después de eliminar
                                        val ejercicios = workout?.getExercise() ?: listOf()
                                        val gson = Gson()
                                        val json = gson.toJson(ejercicios)
                                        saveToFile("ejercicios.json", json)
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        }
                    }
                    .show()
            }

            val exerciseAdapter = ExerciseAdapter(workout?.getExercise() ?: listOf(), onExerciseLongClick)
            recyclerExercises.layoutManager = LinearLayoutManager(requireContext()) // Layout vertical para la lista
            recyclerExercises.adapter = exerciseAdapter // Asigna el adapter al RecyclerView

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Ejercicios")
                .setNegativeButton("Cerrar", null)
                .create()

            btnAddExercise.setOnClickListener {
                val inputView = EditText(requireContext())
                inputView.hint = "Nombre del ejercicio"
                AlertDialog.Builder(requireContext())
                    .setTitle("Añadir ejercicio")
                    .setView(inputView)
                    .setPositiveButton("Añadir") { _, _ ->
                        val nombre = inputView.text.toString().trim()
                        if (nombre.isNotEmpty()) {
                            workout?.addExercise(nombre) // Agrega el ejercicio al modelo
                            exerciseAdapter.notifyDataSetChanged() // Notifica al adapter
                            // Guardar ejercicios en archivo local
                            val ejercicios = workout?.getExercise() ?: listOf()
                            val gson = Gson()
                            val json = gson.toJson(ejercicios)
                            saveToFile("ejercicios.json", json)
                        } else {
                            Toast.makeText(requireContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            dialog.show()
        }

    }
}