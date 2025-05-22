package com.example.my_track_fit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.my_track_fit.model.Workout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    lateinit var workout: Workout
    private val PICK_FILE_REQUEST = 1
    private var selectedFileUri: Uri? = null
    private var userId: Int = 1 // Cambia esto por el ID real del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val btnUpload = findViewById<Button>(R.id.btn_upload_file)
        val btnDownload = findViewById<Button>(R.id.btn_download_file)

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

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

        btnDownload.setOnClickListener {
            downloadArchivo(userId, "ArchivoBody") // Cambia el tipo según lo que quieras descargar
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileUri?.let { uri ->
                uploadArchivo(uri, userId, "ArchivoBody") // Cambia el tipo según lo que quieras subir
            }
        }
    }

    private fun uploadArchivo(uri: Uri, userId: Int, tipo: String) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        val requestFile = RequestBody.create("*/*".toMediaTypeOrNull(), bytes!!)
        val body = MultipartBody.Part.createFormData("archivo", "archivo", requestFile)
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
        val tipoBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tipo)

        RetrofitClient.instance.uploadArchivo(body, userIdBody, tipoBody)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Toast.makeText(this@MainActivity, "Archivo subido", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error al subir archivo", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun downloadArchivo(userId: Int, tipo: String) {
        RetrofitClient.instance.downloadArchivo(userId, tipo)
            .enqueue(object : Callback<okhttp3.ResponseBody> {
                override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                    if (response.isSuccessful) {
                        // Aquí puedes guardar el archivo en el almacenamiento del dispositivo si lo deseas
                        Toast.makeText(this@MainActivity, "Archivo descargado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "No se pudo descargar", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error al descargar archivo", Toast.LENGTH_SHORT).show()
                }
            })
    }
}