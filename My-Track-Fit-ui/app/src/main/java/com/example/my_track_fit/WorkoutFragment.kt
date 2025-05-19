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
import com.example.my_track_fit.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import android.util.Log
import retrofit2.Response
import com.example.my_track_fit.network.ExerciseResponse
import com.example.my_track_fit.network.AddExerciseRequest

class WorkoutFragment : Fragment() {
    // adapter de routine
    private lateinit var adapter: RoutineAdapter

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
                            // Llama a la API para guardar el ejercicio en la base de datos
                            val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
                            val workoutId = sharedPref.getInt("workoutId", -1) // Guarda el workoutId en SharedPreferences al hacer login
                            val apiService = RetrofitClient.instance
                            val request = AddExerciseRequest(nombre, workoutId)
                            apiService.addExercise(request).enqueue(object : Callback<ExerciseResponse> {
                                override fun onResponse(call: Call<ExerciseResponse>, response: Response<ExerciseResponse>) {
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        workout?.addExercise(nombre)
                                        exerciseAdapter.notifyDataSetChanged()
                                    } 
                                    else {
                                        Toast.makeText(requireContext(), "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                                        Log.e("WorkoutFragment", "Error al guardar en la base de datos: ${response.errorBody()?.string()}")
                                    }
                                }
                                override fun onFailure(call: Call<ExerciseResponse>, t: Throwable) {
                                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                         else {
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