package com.example.my_track_fit

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        btnWorkouts.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, WorkoutFragment())
            }
        }

        btnStatistics.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, StatisticsFragment())
            }
        }
    }
}