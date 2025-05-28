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
        private const val ARG_BLOCK_INDEX = "block_index"
        private const val ARG_WEEK_INDEX = "week_index"
        private const val ARG_ROUTINE_INDEX = "routine_index"

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

    private var block: Block? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_block_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routineIndex = arguments?.getInt(ARG_ROUTINE_INDEX) ?: -1
        val weekIndex = arguments?.getInt(ARG_WEEK_INDEX) ?: -1
        val blockIndex = arguments?.getInt(ARG_BLOCK_INDEX) ?: -1

        val workout = (activity as? MainActivity)?.workout
        val routine = workout?.getRoutines()?.getOrNull(routineIndex)
        val week = routine?.getWeeks()?.getOrNull(weekIndex)
        block = week?.getBlockList()?.getOrNull(blockIndex)

        val tvBlockName = view.findViewById<TextView>(R.id.tvBlockName)
        val recyclerView = view.findViewById<RecyclerView>(R.id.exerciseInstanceRecyclerView)
        val btnAddExerciseInstance = view.findViewById<Button>(R.id.btnAddExerciseInstance)

        tvBlockName.text = block?.getName() ?: ""

        val initialInstances = block?.getExerciseInstanceList() ?: mutableListOf()
        val onExerciseInstanceLongClick: (com.example.my_track_fit.model.ExerciseInstance, Int) -> Unit = { instance, position ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Desea eliminar este ejercicio?")
                .setPositiveButton("Aceptar") { _, _ ->
                    block?.deleteExerciseInstance(instance)
                    recyclerView.adapter?.notifyItemRemoved(position)
                    // Guardar rutinas en archivo local después de eliminar una instancia de ejercicio
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

        val exerciseInstanceAdapter = ExerciseInstanceAdapter(
            initialInstances,
            onExerciseInstanceLongClick,
            onExerciseInstanceClick = { instance ->
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
                    // Guardar rutinas en archivo local después de agregar una instancia de ejercicio
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