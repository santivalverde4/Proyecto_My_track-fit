package com.example.my_track_fit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WorkoutFragment : Fragment() {
    // adapter de routine
    private lateinit var adapter: RoutineAdapter

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Acceder al workout de MainActivity
        val workout = (activity as? MainActivity)?.workout

        //cargar datos
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
    
        //funcion para detectar el long click en una rutina
        val onRoutineLongClick: (com.example.my_track_fit.model.Routine, Int) -> Unit = { routine, position ->
            val options = arrayOf("Cambiar nombre", "Eliminar rutina")
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
                                        routine.setName(newName)
                                        adapter.notifyItemChanged(position)
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
                                    workout?.deleteRoutine(routine)
                                    adapter.notifyDataSetChanged()
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
        val routinesRecycler = view.findViewById<RecyclerView>(R.id.routinesListView)
        adapter = RoutineAdapter(
            workout?.getRoutines() ?: listOf(),
            onRoutineLongClick = onRoutineLongClick,
            onRoutineClick = { routine, position ->
                // Navegar al fragmento de detalle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RoutineDetailFragment.newInstance(position))
                    .addToBackStack(null)
                    .commit()
            }
        )
        routinesRecycler.layoutManager = LinearLayoutManager(requireContext())
        routinesRecycler.adapter = adapter

        val addRoutineBtn = view.findViewById<View>(R.id.addRoutine)
        addRoutineBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_routine, null)
            val etRoutineName = dialogView.findViewById<EditText>(R.id.etRoutineName)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.btnAccept).setOnClickListener {
                //manejar el nombre ingresado de la rutina
                val nombre = etRoutineName.text.toString()
                if (nombre.isEmpty()) {
                    // Notifica al usuario que debe escribir al menos un caracter
                    Toast.makeText(requireContext(), "Debe de escribir al menos un carácter!", Toast.LENGTH_SHORT).show()
                }
                else {
                    workout?.addRoutine(nombre)
                    adapter.notifyDataSetChanged()
                    // Guardar rutinas en archivo local
                    val rutinas = workout?.getRoutines() ?: listOf()
                    val gson = Gson()
                    val json = gson.toJson(rutinas)
                    saveToFile("rutinas.json", json)
                    dialog.dismiss()
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
                val options = arrayOf("Cambiar nombre", "Eliminar ejercicio")
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
                                        exercise.setName(newName)
                                        recyclerExercises.adapter?.notifyItemChanged(position)
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
                                        workout?.deleteExercise(exercise)
                                        recyclerExercises.adapter?.notifyDataSetChanged()
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
            recyclerExercises.layoutManager = LinearLayoutManager(requireContext())
            recyclerExercises.adapter = exerciseAdapter

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
                            workout?.addExercise(nombre)
                            exerciseAdapter.notifyDataSetChanged()
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