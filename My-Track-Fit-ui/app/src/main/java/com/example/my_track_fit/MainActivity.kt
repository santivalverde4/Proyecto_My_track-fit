package com.example.my_track_fit

import android.os.Bundle // Para manejar el ciclo de vida de la actividad
import android.widget.ImageButton // Botones de imagen para la navegación inferior
import androidx.appcompat.app.AppCompatActivity // Actividad base para compatibilidad
import androidx.core.view.ViewCompat // Utilidad para manejar insets del sistema
import androidx.core.view.WindowInsetsCompat // Utilidad para manejar insets del sistema
import androidx.fragment.app.commit // Extensión para transacciones de fragmentos
import com.example.my_track_fit.model.Workout // Modelo principal de Workout
import com.example.my_track_fit.fragments.BodyweightFragment // Fragmento para peso corporal
import com.example.my_track_fit.fragments.StatisticsFragment // Fragmento para estadísticas
import com.example.my_track_fit.fragments.UserSettingsFragment // Fragmento para configuración de usuario
import com.example.my_track_fit.fragments.WorkoutFragment // Fragmento para rutinas y ejercicios

class MainActivity : AppCompatActivity() {
    lateinit var workout: Workout // Propiedad global para acceder al Workout en toda la app

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Establece el layout principal de la actividad

        // Crear objeto Workout al iniciar la app (EDIT: aquí se debe de acceder al Workout de la db)
        workout = Workout() // Inicializa el objeto Workout vacío

        // Ajusta los insets del sistema (barra de estado, navegación, etc.) para que la UI no los tape
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mostrar WorkoutFragment por defecto solo la primera vez que se crea la actividad
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, WorkoutFragment()) // Muestra el fragmento de rutinas/ejercicios
            }
        }

        // Referencias a los botones de navegación inferior
        val btnWorkouts = findViewById<ImageButton>(R.id.btn_workouts) // Botón para rutinas/ejercicios
        val btnBodyWeight = findViewById<ImageButton>(R.id.btn_body_weight) // Botón para peso corporal
        val btnStatistics = findViewById<ImageButton>(R.id.btn_statistics) // Botón para estadísticas
        val btnUserSettings = findViewById<ImageButton>(R.id.btn_user_settings) // Botón para configuración de usuario

        // Listener para mostrar el fragmento de rutinas/ejercicios
        btnWorkouts.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, WorkoutFragment())
            }
        }

        // Listener para mostrar el fragmento de peso corporal
        btnBodyWeight.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, BodyweightFragment())
            }
        }

        // Listener para mostrar el fragmento de estadísticas
        btnStatistics.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, StatisticsFragment())
            }
        }

        // Listener para mostrar el fragmento de configuración de usuario
        btnUserSettings.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, UserSettingsFragment())
            }
        }
    }
}