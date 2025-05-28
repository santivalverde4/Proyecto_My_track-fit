package com.example.my_track_fit.fragments

import android.content.Context // Para acceder al contexto y guardar archivos
import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.Button // Botón de UI
import android.widget.ImageButton // Botón de imagen para volver
import android.widget.TextView // Vista para mostrar texto
import androidx.fragment.app.Fragment // Clase base para fragmentos
import androidx.recyclerview.widget.LinearLayoutManager // LayoutManager para listas verticales
import androidx.recyclerview.widget.RecyclerView // Componente para listas eficientes
import com.example.my_track_fit.MainActivity // Actividad principal
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.adapters.SetDataAdapter // Adaptador para los sets
import com.example.my_track_fit.model.ExerciseInstance // Modelo de instancia de ejercicio
import com.google.gson.Gson // Para serializar/deserializar JSON

class ExerciseInstanceDetailFragment : Fragment() {

    companion object {
        private const val ARG_EXERCISE_INSTANCE = "exercise_instance" // Constante para el argumento de la instancia

        // Método para crear una nueva instancia del fragmento con la instancia de ejercicio como argumento
        fun newInstance(exerciseInstance: ExerciseInstance): ExerciseInstanceDetailFragment {
            val fragment = ExerciseInstanceDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_EXERCISE_INSTANCE, exerciseInstance) // Guarda la instancia en el bundle
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var exerciseInstance: ExerciseInstance // Instancia de ejercicio a mostrar
    private lateinit var setDataAdapter: SetDataAdapter // Adaptador para los sets

    // Se llama al crear el fragmento, recupera la instancia de ejercicio desde los argumentos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseInstance = arguments?.getSerializable(ARG_EXERCISE_INSTANCE) as ExerciseInstance
    }

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise_instance_detail, container, false)
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvExerciseName = view.findViewById<TextView>(R.id.tvExerciseName) // Referencia al TextView del nombre del ejercicio
        val setsRecyclerView = view.findViewById<RecyclerView>(R.id.setsRecyclerView) // RecyclerView para los sets
        val btnAddSet = view.findViewById<Button>(R.id.btnAddSet) // Botón para agregar un set
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack) // Botón para volver al menú anterior

        // Botón de volver
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        tvExerciseName.text = exerciseInstance.getExercise().getName() // Muestra el nombre del ejercicio

        val setsDataMap = exerciseInstance.getSetsData() // Obtiene el mapa de sets (clave: número de set, valor: datos del set)
        val setsDataList = setsDataMap.values.toMutableList() // Convierte los valores del mapa a una lista mutable

        // Crea el adaptador para los sets, con callback para eliminar sets
        setDataAdapter = SetDataAdapter(setsDataList) { position ->
            // Eliminar de la lista local y del modelo principal
            val keyToRemove = setsDataMap.keys.elementAt(position) // Obtiene la clave del set a eliminar
            setsDataMap.remove(keyToRemove) // Elimina del mapa
            setsDataList.removeAt(position) // Elimina de la lista
            setDataAdapter.notifyItemRemoved(position) // Notifica al adapter
            // Guardar rutinas en archivo local después de eliminar un set
            val workout = (activity as? MainActivity)?.workout // Obtiene el workout desde la actividad principal
            val rutinas = workout?.getRoutines() ?: listOf() // Obtiene la lista de rutinas
            val gson = Gson() // Instancia de Gson para serializar
            val json = gson.toJson(rutinas) // Convierte la lista de rutinas a JSON
            requireContext().openFileOutput("rutinas.json", Context.MODE_PRIVATE).use {
                it.write(json.toByteArray()) // Escribe el JSON en el archivo
            }
        }

        // Si tienes un callback para editar campos de un set, agrega el guardado ahí también.
        // Por ejemplo, si editas peso, reps o rpe en un método/callback, después de actualizar el set haz:
        val workout = (activity as? MainActivity)?.workout // Obtiene el workout desde la actividad principal
        val rutinas = workout?.getRoutines() ?: listOf() // Obtiene la lista de rutinas
        val gson = com.google.gson.Gson() // Instancia de Gson para serializar
        val json = gson.toJson(rutinas) // Convierte la lista de rutinas a JSON
        requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
            it.write(json.toByteArray()) // Escribe el JSON en el archivo
        }
        
        setsRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // Layout vertical para la lista de sets
        setsRecyclerView.adapter = setDataAdapter // Asigna el adapter al RecyclerView

        btnAddSet.setOnClickListener {
            // Busca el siguiente número de set disponible
            val nextSetNumber = if (setsDataMap.isEmpty()) 1 else (setsDataMap.keys.maxOrNull() ?: 0) + 1
            val newSet = ExerciseInstance.SetData(0, 0, 0) // Crea un nuevo set con valores por defecto
            setsDataMap[nextSetNumber] = newSet // Agrega el nuevo set al mapa
            setsDataList.add(newSet) // Agrega el nuevo set a la lista
            setDataAdapter.notifyItemInserted(setsDataList.size - 1) // Notifica al adapter que se agregó un set
            // Guardar rutinas en archivo local después de agregar un set
            val workout = (activity as? MainActivity)?.workout // Obtiene el workout desde la actividad principal
            val rutinas = workout?.getRoutines() ?: listOf() // Obtiene la lista de rutinas
            val gson = com.google.gson.Gson() // Instancia de Gson para serializar
            val json = gson.toJson(rutinas) // Convierte la lista de rutinas a JSON
            requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
                it.write(json.toByteArray()) // Escribe el JSON en el archivo
            }
        }
    }
}