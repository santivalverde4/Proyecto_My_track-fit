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
import com.example.my_track_fit.network.ExercisesResponse
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
            val exerciseAdapter = ExerciseAdapter(listOf(), { _, _ -> }) // temporal, se reasigna abajo

            // Callback para long click en Exercise (puedes dejarlo igual que ya tienes)
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
                                            // Llama al endpoint para actualizar en la BD
                                            val apiService = RetrofitClient.instance
                                            val request = com.example.my_track_fit.network.UpdateExerciseRequest(newName)
                                            apiService.updateExercise(exercise.id, request).enqueue(object : retrofit2.Callback<com.example.my_track_fit.network.ExerciseResponse> {
                                                override fun onResponse(
                                                    call: retrofit2.Call<com.example.my_track_fit.network.ExerciseResponse>,
                                                    response: retrofit2.Response<com.example.my_track_fit.network.ExerciseResponse>
                                                ) {
                                                    if (response.isSuccessful && response.body()?.success == true) {
                                                        exercise.setName(newName)
                                                        recyclerExercises.adapter?.notifyItemChanged(position)
                                                        Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(requireContext(), "Error al actualizar en la base de datos", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                override fun onFailure(
                                                    call: retrofit2.Call<com.example.my_track_fit.network.ExerciseResponse>,
                                                    t: Throwable
                                                ) {
                                                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                                                }
                                            })
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
                                        val apiService = RetrofitClient.instance
                                        apiService.deleteExercise(exercise.id).enqueue(object : retrofit2.Callback<com.example.my_track_fit.network.ExerciseResponse> {
                                            override fun onResponse(
                                                call: retrofit2.Call<com.example.my_track_fit.network.ExerciseResponse>,
                                                response: retrofit2.Response<com.example.my_track_fit.network.ExerciseResponse>
                                            ) {
                                                if (response.isSuccessful && response.body()?.success == true) {
                                                    // Actualiza la lista desde la BD
                                                    val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
                                                    val workoutId = sharedPref.getInt("workoutId", -1)
                                                    apiService.getExercises(workoutId).enqueue(object : retrofit2.Callback<com.example.my_track_fit.network.ExercisesResponse> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<com.example.my_track_fit.network.ExercisesResponse>,
                                                            response: retrofit2.Response<com.example.my_track_fit.network.ExercisesResponse>
                                                        ) {
                                                            if (response.isSuccessful && response.body() != null) {
                                                                exerciseAdapter.updateList(response.body()!!.exercises)
                                                            }
                                                        }
                                                        override fun onFailure(
                                                            call: retrofit2.Call<com.example.my_track_fit.network.ExercisesResponse>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(requireContext(), "Error al actualizar ejercicios", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })
                                                    Toast.makeText(requireContext(), "Ejercicio eliminado", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(requireContext(), "Error al eliminar en la base de datos", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            override fun onFailure(
                                                call: retrofit2.Call<com.example.my_track_fit.network.ExerciseResponse>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        }
                    }
                    .show()
            }
            
            exerciseAdapter.setOnExerciseLongClick(onExerciseLongClick)
            recyclerExercises.layoutManager = LinearLayoutManager(requireContext())
            recyclerExercises.adapter = exerciseAdapter

            // Obtén el workoutId y la instancia de la API
            val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
            val workoutId = sharedPref.getInt("workoutId", -1)
            val apiService = RetrofitClient.instance

            // Pide la lista actualizada de ejercicios ANTES de mostrar el diálogo
            apiService.getExercises(workoutId).enqueue(object : Callback<ExercisesResponse> {
                override fun onResponse(call: Call<ExercisesResponse>, response: Response<ExercisesResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        exerciseAdapter.updateList(response.body()!!.exercises)
                    }
                    // Muestra el diálogo después de actualizar la lista
                    showExerciseDialog(dialogView, btnAddExercise, exerciseAdapter, workoutId, apiService)
                }
                override fun onFailure(call: Call<ExercisesResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error al cargar ejercicios", Toast.LENGTH_SHORT).show()
                    showExerciseDialog(dialogView, btnAddExercise, exerciseAdapter, workoutId, apiService)
                }
            })
        }
    }

    private fun showExerciseDialog(
        dialogView: View,
        btnAddExercise: Button,
        exerciseAdapter: ExerciseAdapter,
        workoutId: Int,
        apiService: com.example.my_track_fit.network.ApiService
    ) {
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
                        val request = AddExerciseRequest(nombre, workoutId)
                        apiService.addExercise(request).enqueue(object : Callback<ExerciseResponse> {
                            override fun onResponse(call: Call<ExerciseResponse>, response: Response<ExerciseResponse>) {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    // Actualiza la lista después de agregar
                                    apiService.getExercises(workoutId).enqueue(object : Callback<ExercisesResponse> {
                                        override fun onResponse(call: Call<ExercisesResponse>, response: Response<ExercisesResponse>) {
                                            if (response.isSuccessful && response.body() != null) {
                                                exerciseAdapter.updateList(response.body()!!.exercises)
                                            }
                                        }
                                        override fun onFailure(call: Call<ExercisesResponse>, t: Throwable) {
                                            Toast.makeText(requireContext(), "Error al actualizar ejercicios", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                } else {
                                    Toast.makeText(requireContext(), "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                                    Log.e("WorkoutFragment", "Error al guardar en la base de datos: ${response.errorBody()?.string()}")
                                }
                            }
                            override fun onFailure(call: Call<ExerciseResponse>, t: Throwable) {
                                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                            }
                        })
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