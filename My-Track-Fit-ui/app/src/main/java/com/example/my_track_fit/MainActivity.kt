package com.example.my_track_fit

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.my_track_fit.model.Workout
import android.util.Log
import com.example.my_track_fit.network.RetrofitClient
import com.example.my_track_fit.network.WorkoutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var workout: Workout // <-- propiedad de clase, no variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crear objeto Workout al iniciar la app (EDIT: aqui se debe de acceder al Workout de la db)
        workout = Workout()
        // Usa el mismo nombre de preferencias y tipo de dato
        val sharedPref = getSharedPreferences("MyTrackFitPrefs", MODE_PRIVATE)
        val workoutId = sharedPref.getInt("workoutId", -1)
        Log.d("MainActivity", "workoutId en SharedPreferences: $workoutId")

        if (workoutId != -1) {
            RetrofitClient.instance.getWorkout(workoutId)
                .enqueue(object : Callback<WorkoutResponse> {
                    override fun onResponse(call: Call<WorkoutResponse>, response: Response<WorkoutResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            workout = Workout(
                                id = data.id,
                                routineList = data.routines.toMutableList(),
                                exerciseList = data.exercises.toMutableList()
                            )
                            // Aquí puedes continuar con la lógica que depende de workout
                        } else {
                            // Manejar error
                            workout = Workout()
                        }
                    }
                    override fun onFailure(call: Call<WorkoutResponse>, t: Throwable) {
                        // Manejar error de red
                        workout = Workout()
                    }
                })
        } 
        else {
            workout = Workout()
        }

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