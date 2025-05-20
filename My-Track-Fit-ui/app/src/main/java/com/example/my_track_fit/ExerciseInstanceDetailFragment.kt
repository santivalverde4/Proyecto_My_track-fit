package com.example.my_track_fit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.ExerciseInstance

class ExerciseInstanceDetailFragment : Fragment() {

    companion object {
        private const val ARG_EXERCISE_INSTANCE = "exercise_instance"

        fun newInstance(exerciseInstance: ExerciseInstance): ExerciseInstanceDetailFragment {
            val fragment = ExerciseInstanceDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_EXERCISE_INSTANCE, exerciseInstance)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var exerciseInstance: ExerciseInstance
    private lateinit var setDataAdapter: SetDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseInstance = arguments?.getSerializable(ARG_EXERCISE_INSTANCE) as ExerciseInstance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise_instance_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvExerciseName = view.findViewById<TextView>(R.id.tvExerciseName)
        val setsRecyclerView = view.findViewById<RecyclerView>(R.id.setsRecyclerView)
        val btnAddSet = view.findViewById<Button>(R.id.btnAddSet)

        tvExerciseName.text = exerciseInstance.getExercise().getName()
        val setsDataMap = exerciseInstance.getSetsData()
        val setsDataList = setsDataMap.values.toMutableList()
        setDataAdapter = SetDataAdapter(setsDataList) { position ->
            // Eliminar de la lista local y del modelo principal
            val keyToRemove = setsDataMap.keys.elementAt(position)
            setsDataMap.remove(keyToRemove)
            setsDataList.removeAt(position)
            setDataAdapter.notifyItemRemoved(position)
            // Guardar rutinas en archivo local después de eliminar un set
            val workout = (activity as? MainActivity)?.workout
            val rutinas = workout?.getRoutines() ?: listOf()
            val gson = com.google.gson.Gson()
            val json = gson.toJson(rutinas)
            requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        }

        // Si tienes un callback para editar campos de un set, agrega el guardado ahí también.
        // Por ejemplo, si editas peso, reps o rpe en un método/callback, después de actualizar el set haz:
        val workout = (activity as? MainActivity)?.workout
        val rutinas = workout?.getRoutines() ?: listOf()
        val gson = com.google.gson.Gson()
        val json = gson.toJson(rutinas)
        requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
        
        setsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setsRecyclerView.adapter = setDataAdapter

        btnAddSet.setOnClickListener {
            // Busca el siguiente número de set disponible
            val nextSetNumber = if (setsDataMap.isEmpty()) 1 else (setsDataMap.keys.maxOrNull() ?: 0) + 1
            val newSet = ExerciseInstance.SetData(0, 0, 0)
            setsDataMap[nextSetNumber] = newSet
            setsDataList.add(newSet)
            setDataAdapter.notifyItemInserted(setsDataList.size - 1)
            // Guardar rutinas en archivo local después de agregar un set
            val workout = (activity as? MainActivity)?.workout
            val rutinas = workout?.getRoutines() ?: listOf()
            val gson = com.google.gson.Gson()
            val json = gson.toJson(rutinas)
            requireContext().openFileOutput("rutinas.json", android.content.Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        }
    }
}