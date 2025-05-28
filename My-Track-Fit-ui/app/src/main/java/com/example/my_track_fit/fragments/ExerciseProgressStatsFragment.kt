package com.example.my_track_fit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.my_track_fit.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ExerciseProgressStatsFragment(
    private val exerciseName: String,
    private val progressByWeek: List<Pair<String, Float>>
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise_progress_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChart = view.findViewById<LineChart>(R.id.lineChartExerciseProgress)

        val entries = progressByWeek.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }
        val weekLabels = progressByWeek.map { it.first }

        val dataSet = LineDataSet(entries, "Progreso de $exerciseName")
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 12f

        lineChart.data = LineData(dataSet)
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(weekLabels)
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.labelRotationAngle = -45f
        lineChart.xAxis.setLabelCount(weekLabels.size, true)
        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false

        lineChart.invalidate()
    }
}