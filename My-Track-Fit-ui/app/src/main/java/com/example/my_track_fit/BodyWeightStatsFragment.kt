package com.example.my_track_fit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.my_track_fit.model.BodyWeight
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class BodyWeightStatsFragment(
    private val bodyWeight: BodyWeight
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_body_weight_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        // Ordena las marcas por fecha
        val marks = bodyWeight.getBodyWeightMarks().sortedBy { it.getDate() }

        // Prepara los datos para el gráfico
        val entries = marks.mapIndexed { index, mark ->
            Entry(index.toFloat(), mark.getBodyWeightMark().toFloat())
        }

        // Etiquetas del eje X (fechas)
        val xLabels = marks.map { it.getDate().toString() }

        val dataSet = LineDataSet(entries, "Peso corporal")
        dataSet.setDrawValues(true) // Muestra los valores del eje Y en los puntos
        dataSet.valueTextSize = 12f

        lineChart.data = LineData(dataSet)

        // Formateador para mostrar las fechas en el eje X
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.labelRotationAngle = -45f // Opcional: gira las etiquetas para mejor visibilidad
        lineChart.xAxis.setLabelCount(xLabels.size, true)

        lineChart.axisRight.isEnabled = false // Solo eje Y izquierdo
        lineChart.description.isEnabled = false // Quita la descripción por defecto

        lineChart.invalidate()
    }
}