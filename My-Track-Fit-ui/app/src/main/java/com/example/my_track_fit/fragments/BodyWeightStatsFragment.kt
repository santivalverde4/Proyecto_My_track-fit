package com.example.my_track_fit.fragments

import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import androidx.fragment.app.Fragment // Clase base para fragmentos
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.example.my_track_fit.model.BodyWeight // Modelo de BodyWeight
import com.github.mikephil.charting.charts.LineChart // Vista para gráficos de líneas
import com.github.mikephil.charting.data.Entry // Punto de datos para el gráfico
import com.github.mikephil.charting.data.LineData // Datos para el gráfico de líneas
import com.github.mikephil.charting.data.LineDataSet // Conjunto de datos para el gráfico de líneas
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter // Formateador para etiquetas del eje X

// Fragmento para mostrar estadísticas de peso corporal en un gráfico de líneas
class BodyWeightStatsFragment(
    private val bodyWeight: BodyWeight // Instancia del modelo BodyWeight con las marcas a graficar
) : Fragment() {

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_body_weight_stats, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBack = view.findViewById<android.widget.ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val lineChart = view.findViewById<LineChart>(R.id.lineChart) // Referencia al gráfico de líneas

        // Ordena las marcas por fecha
        val marks = bodyWeight.getBodyWeightMarks().sortedBy { it.getDate() }

        // Prepara los datos para el gráfico (cada Entry es un punto: índice en X, peso en Y)
        val entries = marks.mapIndexed { index, mark ->
            Entry(index.toFloat(), mark.getBodyWeightMark().toFloat())
        }

        // Etiquetas del eje X (fechas de cada marca)
        val xLabels = marks.map { it.getDate().toString() }

        val dataSet = LineDataSet(entries, "Peso corporal") // Crea el conjunto de datos para el gráfico
        dataSet.setDrawValues(entries.size <= 15) // Solo muestra los valores si hay pocos puntos
        dataSet.valueTextSize = 12f // Tamaño del texto de los valores

        lineChart.data = LineData(dataSet) // Asigna los datos al gráfico

        // Formateador para mostrar las fechas en el eje X
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        lineChart.xAxis.granularity = 1f // Un valor por cada marca
        lineChart.xAxis.labelRotationAngle = -45f // Opcional: gira las etiquetas para mejor visibilidad
        //lineChart.xAxis.setLabelCount(xLabels.size, true) // Muestra todas las etiquetas

        lineChart.axisRight.isEnabled = false // Solo muestra el eje Y izquierdo
        lineChart.description.isEnabled = false // Quita la descripción por defecto
        lineChart.setExtraTopOffset(32f)

        lineChart.invalidate() // Refresca el gráfico para mostrar los datos
    }
}