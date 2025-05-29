package com.example.my_track_fit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.Block
import android.widget.Toast
import android.app.AlertDialog
import com.example.my_track_fit.MainActivity
import com.example.my_track_fit.R
import com.example.my_track_fit.adapters.ExerciseInstanceAdapter

/**
 * Fragmento que muestra el detalle de un bloque, incluyendo su nombre,
 * la lista de instancias de ejercicios y opciones para agregar o eliminar ejercicios.
 */
class BlockDetailFragment : Fragment() {

    companion object {
        private const val ARG_BLOCK_INDEX = "block_index"     // Argumento: índice del bloque
        private const val ARG_WEEK_INDEX = "week_index"       // Argumento: índice de la semana
        private const val ARG_ROUTINE_INDEX = "routine_index" // Argumento: índice de la rutina

        /**
         * Crea una nueva instancia del fragmento con los índices necesarios.
         */
        fun newInstance(routineIndex: Int, weekIndex: Int, blockIndex: Int): BlockDetailFragment {
            val fragment = BlockDetailFragment()
            val args = Bundle()
            args.putInt(ARG_ROUTINE_INDEX, routineIndex)
            args.putInt(ARG_WEEK_INDEX, weekIndex)
            args.putInt(ARG_BLOCK_INDEX, blockIndex)
            fragment.arguments = args
            return fragment
        }
    }

    private var block: Block? = null // Referencia al bloque actual

    /**
     * Infla el layout del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_block_detail, container, false)
    }

    /**
     * Inicializa la vista y la lógica del fragmento una vez creada la vista.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera los índices de rutina, semana y bloque desde los argumentos
        val routineIndex = arguments?.getInt(ARG_ROUTINE_INDEX) ?: -1
        val weekIndex = arguments?.getInt(ARG_WEEK_INDEX) ?: -1
        val blockIndex = arguments?.getInt(ARG_BLOCK_INDEX) ?: -1

        // Obtiene el bloque correspondiente desde la estructura de datos principal
        val workout = (activity as? MainActivity)?.workout
        val routine = workout?.getRoutines()?.getOrNull(routineIndex)
        val week = routine?.getWeeks()?.getOrNull(weekIndex)
        block = week?.getBlockList()?.getOrNull(blockIndex)

        // Referencias a los elementos de la UI
        val tvBlockName = view.findViewById<TextView>(R.id.tvBlockName) // Nombre del bloque
        val recyclerView = view.findViewById<RecyclerView>(R.id.exerciseInstanceRecyclerView) // Lista de ejercicios
        val btnAddExerciseInstance = view.findViewById<Button>(R.id.btnAddExerciseInstance) // Botón para agregar ejercicio
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack) // Botón para volver atrás

        // Configura el botón de volver para regresar al menú anterior
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Muestra el nombre del bloque
        //tvBlockName.text = block?.getName() ?: ""
        
        tvBlockName.text = "Bloque" //muestra la palbra Bloque

        // Obtiene la lista inicial de instancias de ejercicios del bloque
        val initialInstances = block?.getExerciseInstanceList() ?: mutableListOf()

        /**
         * Callback para eliminar una instancia de ejercicio al hacer long click.
         * Muestra un diálogo de confirmación y, si se acepta, elimina el ejercicio y guarda los cambios.
         */
        val onExerciseInstanceLongClick: (com.example.my_track_fit.model.ExerciseInstance, Int) -> Unit = { instance, position ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Desea eliminar este ejercicio?")
                .setPositiveButton("Aceptar") { _, _ ->
                    block?.deleteExerciseInstance(instance)
                    recyclerView.adapter?.notifyItemRemoved(position)
                    // Guarda los cambios en el archivo local
                    val workout = (activity as? MainActivity)?.workout
                    val rutinas = workout?.getRoutines() ?: listOf()
                    val gson = com.google.gson.Gson()
                    val json = gson.toJson(rutinas)
                    requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
                        it.write(json.toByteArray())
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Crea el adaptador para la lista de instancias de ejercicios
        val exerciseInstanceAdapter = ExerciseInstanceAdapter(
            initialInstances,
            onExerciseInstanceLongClick,
            onExerciseInstanceClick = { instance ->
                // Al hacer click, abre el detalle de la instancia de ejercicio
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        ExerciseInstanceDetailFragment.newInstance(instance)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = exerciseInstanceAdapter

        /**
         * Acción del botón para agregar una nueva instancia de ejercicio al bloque.
         * Muestra un diálogo para seleccionar el ejercicio y guarda los cambios.
         */
        btnAddExerciseInstance.setOnClickListener {
            val exerciseList = workout?.getExercise() ?: listOf()
            if (exerciseList.isEmpty()) {
                Toast.makeText(requireContext(), "No hay ejercicios disponibles", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val exerciseNames = exerciseList.map { it.getName() }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona un ejercicio")
                .setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    block?.addExerciseInstance(selectedExercise)
                    exerciseInstanceAdapter.notifyDataSetChanged()
                    // Guarda los cambios en el archivo local
                    val workout = (activity as? MainActivity)?.workout
                    val rutinas = workout?.getRoutines() ?: listOf()
                    val gson = com.google.gson.Gson()
                    val json = gson.toJson(rutinas)
                    requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
                        it.write(json.toByteArray())
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}