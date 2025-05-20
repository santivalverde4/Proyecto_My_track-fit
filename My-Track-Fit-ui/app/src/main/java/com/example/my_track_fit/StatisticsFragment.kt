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

        btnBodyWeightStats.setOnClickListener {
            val bodyWeight = loadBodyWeightFromFile()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BodyWeightStatsFragment(bodyWeight)) // <-- aquí el cambio
                .addToBackStack(null)
                .commit()
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