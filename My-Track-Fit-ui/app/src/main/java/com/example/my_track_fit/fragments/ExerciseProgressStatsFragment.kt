package com.example.my_track_fit.fragments

import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import androidx.fragment.app.Fragment // Clase base para fragmentos
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.github.mikephil.charting.charts.LineChart // Vista para gráficos de líneas
import com.github.mikephil.charting.data.Entry // Punto de datos para el gráfico
import com.github.mikephil.charting.data.LineData // Datos para el gráfico de líneas
import com.github.mikephil.charting.data.LineDataSet // Conjunto de datos para el gráfico de líneas
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter // Formateador para etiquetas del eje X

// Fragmento para mostrar el progreso de un ejercicio en un gráfico de líneas
class ExerciseProgressStatsFragment(
    private val exerciseName: String, // Nombre del ejercicio a mostrar
    private val progressByWeek: List<Pair<String, Float>> // Lista de pares (semana, valor de progreso)
) : Fragment() {

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise_progress_stats, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChart = view.findViewById<LineChart>(R.id.lineChartExerciseProgress) // Referencia al gráfico de líneas

        // Prepara los datos para el gráfico (cada Entry es un punto: índice en X, valor de progreso en Y)
        val entries = progressByWeek.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }
        // Etiquetas del eje X (nombre de la semana)
        val weekLabels = progressByWeek.map { it.first }

        val dataSet = LineDataSet(entries, "Progreso de $exerciseName") // Crea el conjunto de datos para el gráfico
        dataSet.setDrawValues(true) // Muestra los valores del eje Y en los puntos
        dataSet.valueTextSize = 12f // Tamaño del texto de los valores

        lineChart.data = LineData(dataSet) // Asigna los datos al gráfico

        // Formateador para mostrar las semanas en el eje X
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(weekLabels)
        lineChart.xAxis.granularity = 1f // Un valor por cada semana
        lineChart.xAxis.labelRotationAngle = -45f // Gira las etiquetas para mejor visibilidad
        lineChart.xAxis.setLabelCount(weekLabels.size, true) // Muestra todas las etiquetas

        lineChart.axisRight.isEnabled = false // Solo muestra el eje Y izquierdo
        lineChart.description.isEnabled = false // Quita la descripción por defecto

        lineChart.invalidate() // Refresca el gráfico para mostrar los datos
    }
}