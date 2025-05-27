package com.example.my_track_fit

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.my_track_fit.model.Workout
import com.example.my_track_fit.WorkoutFragment
import com.example.my_track_fit.BodyweightFragment
import com.example.my_track_fit.StatisticsFragment
import com.example.my_track_fit.UserSettingsFragment

class MainActivity : AppCompatActivity() {
    lateinit var workout: Workout // <-- propiedad de clase, no variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crear objeto Workout al iniciar la app (EDIT: aqui se debe de acceder al Workout de la db)
        workout = Workout()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mostrar WorkoutFragment por defecto solo la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, WorkoutFragment())
            }
        }

        val btnWorkouts = findViewById<ImageButton>(R.id.btn_workouts)
        val btnBodyWeight = findViewById<ImageButton>(R.id.btn_body_weight)
        val btnStatistics = findViewById<ImageButton>(R.id.btn_statistics)
        val btnUserSettings = findViewById<ImageButton>(R.id.btn_user_settings)

        btnWorkouts.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, WorkoutFragment())
            }
        }

        btnBodyWeight.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, BodyweightFragment())
            }
        }

        btnStatistics.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, StatisticsFragment())
            }
        }

        btnUserSettings.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, UserSettingsFragment())
            }
        }
    }
}