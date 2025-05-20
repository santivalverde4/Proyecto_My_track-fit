package com.example.my_track_fit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import java.io.File
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class UserSettingsFragment : Fragment() {
    private val client = OkHttpClient()
    private val baseUrl = "http://TU_API_URL:PUERTO" // Cambia esto por tu URL real

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "Correo no disponible")
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        tvUserEmail.text = userEmail

        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // Borrar datos de sesión
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            // Ir a LoginActivity y cerrar la actividad actual
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        val btnDownload = view.findViewById<Button>(R.id.btn_download_cloud)
        val btnUpload = view.findViewById<Button>(R.id.btn_upload_cloud)

        btnDownload.setOnClickListener {
            downloadFilesFromCloud()
        }

        btnUpload.setOnClickListener {
            uploadFilesToCloud()
        }
    }

    private fun downloadFilesFromCloud() {
        val files = listOf(
            "bodyweight.json" to "ArchivoBody",
            "rutinas.json" to "ArchivoRutina",
            "ejercicios.json" to "ArchivoEjercicio"
        )
        val userEmail = getUserEmail()
        val request = Request.Builder()
            .url("$baseUrl/archivosusuario/$userEmail") // Ajusta el endpoint según tu API
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error al descargar archivos", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    val jsonObject = JSONObject(json ?: "{}")
                    for ((fileName, apiField) in files) {
                        val value = jsonObject.optString(apiField, "")
                        val file = File(requireContext().filesDir, fileName)
                        file.writeText(value)
                        requireActivity().runOnUiThread {
                            Toast.makeText(context, "$fileName descargado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Error al descargar archivos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun uploadFilesToCloud() {
        val files = listOf(
            "bodyweight.json" to "ArchivoBody",
            "rutinas.json" to "ArchivoRutina",
            "ejercicios.json" to "ArchivoEjercicio"
        )
        val userEmail = getUserEmail()
        val jsonBody = JSONObject()
        jsonBody.put("Correo", userEmail)
        for ((fileName, apiField) in files) {
            val file = File(requireContext().filesDir, fileName)
            jsonBody.put(apiField, if (file.exists()) file.readText() else "")
        }
        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            jsonBody.toString()
        )
        val request = Request.Builder()
            .url("$baseUrl/archivosusuario/$userEmail") // Ajusta el endpoint según tu API
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error al subir archivos", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Archivos subidos", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al subir archivos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getUserEmail(): String {
        val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
        return sharedPref.getString("userEmail", "default") ?: "default"
    }
}