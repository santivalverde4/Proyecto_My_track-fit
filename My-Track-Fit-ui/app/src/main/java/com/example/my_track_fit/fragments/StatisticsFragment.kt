package com.example.my_track_fit.fragments

import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import androidx.fragment.app.Fragment // Clase base para fragmentos
import android.widget.Button // Botón de UI
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.model.BodyWeight // Modelo de BodyWeight
import com.example.my_track_fit.model.Mark // Modelo de marca de peso
import com.example.my_track_fit.adapters.LocalDateAdapter // Adaptador para fechas LocalDate

class StatisticsFragment : Fragment() {
    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBodyWeightStats = view.findViewById<Button>(R.id.btnBodyWeightStats) // Botón para ver estadísticas de peso corporal
        val btnExerciseStats = view.findViewById<Button>(R.id.btnExerciseStats) // Botón para ver estadísticas de ejercicios

        // Listener para el botón de estadísticas de peso corporal
        btnBodyWeightStats.setOnClickListener {
            val bodyWeight = loadBodyWeightFromFile() // Carga los datos de peso corporal desde archivo
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BodyWeightStatsFragment(bodyWeight)) // Navega al fragmento de estadísticas de peso corporal
                .addToBackStack(null)
                .commit()
        }

        // Listener para el botón de estadísticas de ejercicios
        btnExerciseStats.setOnClickListener {
            val rutinas = loadRoutinesFromFile() // Carga las rutinas desde archivo
            if (rutinas.isEmpty()) {
                // Si no hay rutinas, muestra un diálogo informativo
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Rutinas")
                    .setMessage("No hay rutinas guardadas.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }
            val rutinaNombres = rutinas.map { it.getName() }.toTypedArray() // Obtiene los nombres de las rutinas
            // Diálogo para seleccionar una rutina
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
                        // Si no hay ejercicios, muestra un diálogo informativo
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Ejercicios")
                            .setMessage("No hay ejercicios en esta rutina.")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        // Diálogo para seleccionar un ejercicio de la rutina
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Ejercicios de la rutina")
                            .setItems(ejercicios.toTypedArray()) { _, ejercicioIdx ->
                                val ejercicioSeleccionado = ejercicios[ejercicioIdx]
                                val semanas = rutinaSeleccionada.getWeeks()
                                // Calcula el progreso por semana para el ejercicio seleccionado
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
                                    nombreSemana to progreso // Par (nombre de la semana, progreso)
                                }
                                // Navegar al fragmento de gráfica de progreso del ejercicio
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
            val json = requireContext().openFileInput("rutinas.json").bufferedReader().use { it.readText() } // Lee el archivo como texto
            if (json.isNotEmpty()) {
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<com.example.my_track_fit.model.Routine>>() {}.type
                gson.fromJson(json, type) // Deserializa la lista de rutinas
            } else {
                emptyList() // Si el archivo está vacío, devuelve una lista vacía
            }
        } catch (e: Exception) {
            emptyList() // Si hay error, devuelve una lista vacía
        }
    }

    // Lee el archivo bodyweight.json y devuelve un BodyWeight
    private fun loadBodyWeightFromFile(): BodyWeight {
        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate::class.java, LocalDateAdapter()) // Usa el adaptador para fechas
            .create()
        val marksList: MutableList<Mark> = try {
            val json = requireContext().openFileInput("bodyweight.json").bufferedReader().use { it.readText() } // Lee el archivo como texto
            val type = com.google.gson.reflect.TypeToken.getParameterized(MutableList::class.java, Mark::class.java).type // Tipo para deserializar la lista
            gson.fromJson(json, type) ?: mutableListOf() // Deserializa o crea lista vacía
        } catch (e: Exception) {
            mutableListOf() // Si hay error, crea lista vacía
        }
        return BodyWeight(marksList) // Devuelve el modelo BodyWeight con la lista de marcas
    }
}