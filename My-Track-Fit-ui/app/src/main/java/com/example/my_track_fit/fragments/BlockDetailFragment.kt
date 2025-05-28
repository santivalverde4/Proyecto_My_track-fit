package com.example.my_track_fit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class BlockDetailFragment : Fragment() {
    companion object {
        private const val ARG_BLOCK_INDEX = "block_index" // Constante para el índice del bloque
        private const val ARG_WEEK_INDEX = "week_index" // Constante para el índice de la semana
        private const val ARG_ROUTINE_INDEX = "routine_index" // Constante para el índice de la rutina

        // Método para crear una nueva instancia del fragmento con los argumentos necesarios
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

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_block_detail, container, false)
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtiene los índices de rutina, semana y bloque desde los argumentos
        val routineIndex = arguments?.getInt(ARG_ROUTINE_INDEX) ?: -1
        val weekIndex = arguments?.getInt(ARG_WEEK_INDEX) ?: -1
        val blockIndex = arguments?.getInt(ARG_BLOCK_INDEX) ?: -1

        // Obtiene el workout y navega hasta el bloque correspondiente
        val workout = (activity as? MainActivity)?.workout
        val routine = workout?.getRoutines()?.getOrNull(routineIndex)
        val week = routine?.getWeeks()?.getOrNull(weekIndex)
        block = week?.getBlockList()?.getOrNull(blockIndex)

        // Referencias a vistas del layout
        val tvBlockName = view.findViewById<TextView>(R.id.tvBlockName)
        val recyclerView = view.findViewById<RecyclerView>(R.id.exerciseInstanceRecyclerView)
        val btnAddExerciseInstance = view.findViewById<Button>(R.id.btnAddExerciseInstance)

        // Muestra el nombre del bloque
        tvBlockName.text = block?.getName() ?: ""

        // Obtiene la lista inicial de instancias de ejercicio del bloque
        val initialInstances = block?.getExerciseInstanceList() ?: mutableListOf()

        // Callback para long click en una instancia de ejercicio (eliminar)
        val onExerciseInstanceLongClick: (com.example.my_track_fit.model.ExerciseInstance, Int) -> Unit = { instance, position ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Desea eliminar este ejercicio?")
                .setPositiveButton("Aceptar") { _, _ ->
                    block?.deleteExerciseInstance(instance) // Elimina la instancia del bloque
                    recyclerView.adapter?.notifyItemRemoved(position) // Notifica al adapter
                    // Guarda las rutinas en archivo local después de eliminar
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

        // Crea el adapter para la lista de instancias de ejercicio
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Layout vertical para la lista
        recyclerView.adapter = exerciseInstanceAdapter // Asigna el adapter al RecyclerView

        // Listener para el botón de agregar nueva instancia de ejercicio
        btnAddExerciseInstance.setOnClickListener {
            val exerciseList = workout?.getExercise() ?: listOf() // Obtiene la lista de ejercicios disponibles
            if (exerciseList.isEmpty()) {
                Toast.makeText(requireContext(), "No hay ejercicios disponibles", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val exerciseNames = exerciseList.map { it.getName() }.toTypedArray() // Nombres de los ejercicios
            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona un ejercicio")
                .setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which] // Obtiene el ejercicio seleccionado
                    block?.addExerciseInstance(selectedExercise) // Agrega la instancia al bloque
                    exerciseInstanceAdapter.notifyDataSetChanged() // Notifica al adapter
                    // Guarda las rutinas en archivo local después de agregar
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