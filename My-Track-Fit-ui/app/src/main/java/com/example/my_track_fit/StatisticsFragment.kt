package com.example.my_track_fit.com.example.my_track_fit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.my_track_fit.R
import android.widget.Button
import com.example.my_track_fit.model.BodyWeight
import com.example.my_track_fit.BodyWeightStatsFragment
import com.example.my_track_fit.model.Mark
import com.example.my_track_fit.ExerciseProgressStatsFragment

class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBodyWeightStats = view.findViewById<Button>(R.id.btnBodyWeightStats)
        val btnExerciseStats = view.findViewById<Button>(R.id.btnExerciseStats)

        btnBodyWeightStats.setOnClickListener {
            val bodyWeight = loadBodyWeightFromFile()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BodyWeightStatsFragment(bodyWeight)) // <-- aquí el cambio
                .addToBackStack(null)
                .commit()
        }

        btnExerciseStats.setOnClickListener {
            val rutinas = loadRoutinesFromFile()
            if (rutinas.isEmpty()) {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Rutinas")
                    .setMessage("No hay rutinas guardadas.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }
            val rutinaNombres = rutinas.map { it.getName() }.toTypedArray()
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Selecciona una rutina")
                .setItems(rutinaNombres) { _, rutinaIdx ->
                    val rutinaSeleccionada = rutinas[rutinaIdx]
                    // Obtener ejercicios únicos de todos los bloques de todas las semanas 
                    val ejercicios = rutinaSeleccionada.getWeeks()
                        .flatMap { semana -> 
                            semana.getBlockList().flatMap { it.getExerciseInstanceList() }
                        }
                        .map { it.getExercise().getName() }
                        .distinct()
                    if (ejercicios.isEmpty()) {
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Ejercicios")
                            .setMessage("No hay ejercicios en esta rutina.")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Ejercicios de la rutina")
                            .setItems(ejercicios.toTypedArray()) { _, ejercicioIdx ->
                                val ejercicioSeleccionado = ejercicios[ejercicioIdx]
                                val semanas = rutinaSeleccionada.getWeeks()
                                val progresoPorSemana = semanas.mapIndexed { semanaIdx, semana ->
                                    val nombreSemana = "Semana ${semanaIdx + 1}"
                                    // Buscar todas las instancias de ese ejercicio en todos los bloques de la semana
                                    val instancias = semana.getBlockList()
                                        .flatMap { it.getExerciseInstanceList() }
                                        .filter { it.getExercise().getName() == ejercicioSeleccionado }
                                    // Sumar (peso * repeticiones) de todos los sets de todas las instancias
                                    val progreso = instancias.sumOf { instancia ->
                                        instancia.getSetsData().values.sumOf { set ->
                                            (set.weight ?: 0) * (set.reps ?: 0)
                                        }
                                    }.toFloat()
                                    nombreSemana to progreso
                                }
                                // Navegar al fragmento de gráfica
                                parentFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.fragment_container,
                                        ExerciseProgressStatsFragment(ejercicioSeleccionado, progresoPorSemana)
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    // Función para leer rutinas desde rutinas.json
    private fun loadRoutinesFromFile(): List<com.example.my_track_fit.model.Routine> {
        return try {
            val json = requireContext().openFileInput("rutinas.json").bufferedReader().use { it.readText() }
            if (json.isNotEmpty()) {
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<com.example.my_track_fit.model.Routine>>() {}.type
                gson.fromJson(json, type)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Lee el archivo bodyweight.json y devuelve un BodyWeight
    private fun loadBodyWeightFromFile(): BodyWeight {
        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate::class.java, com.example.my_track_fit.LocalDateAdapter())
            .create()
        val marksList: MutableList<Mark> = try {
            val json = requireContext().openFileInput("bodyweight.json").bufferedReader().use { it.readText() }
            val type = com.google.gson.reflect.TypeToken.getParameterized(MutableList::class.java, Mark::class.java).type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
        return BodyWeight(marksList)
    }
}