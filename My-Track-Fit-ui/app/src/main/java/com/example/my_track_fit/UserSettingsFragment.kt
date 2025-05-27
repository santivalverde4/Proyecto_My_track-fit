package com.example.my_track_fit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson

class UserSettingsFragment : Fragment() {
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

        val btnChangePassword = view.findViewById<Button>(R.id.btn_change_password)
        btnChangePassword.setOnClickListener {
            // Envía petición al backend para enviar el correo de cambio de contraseña
            val email = userEmail ?: return@setOnClickListener
            Thread {
                try {
                    val url = java.net.URL("http://10.0.2.2:3000/api/request-password-reset")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    val json = """{"email":"$email"}"""
                    conn.outputStream.use { it.write(json.toByteArray()) }
                    val response = conn.inputStream.bufferedReader().readText()
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Correo de cambio de contraseña enviado", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error enviando correo", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        // Botón para cargar archivos a la nube
        val btnUploadFiles = view.findViewById<Button>(R.id.btn_load_routines_cloud)
        btnUploadFiles.setOnClickListener {
            Thread {
                try {
                    val archivoBody = readFileContent("bodyweight.json")
                    val archivoRutina = readFileContent("rutinas.json")
                    val archivoEjercicio = readFileContent("ejercicios.json")
                    val email = userEmail ?: ""
                    val json = Gson().toJson(
                        mapOf(
                            "email" to email,
                            "ArchivoBody" to archivoBody,
                            "ArchivoRutina" to archivoRutina,
                            "ArchivoEjercicio" to archivoEjercicio
                        )
                    )
                    val url = java.net.URL("http://10.0.2.2:3000/api/upload-user-files")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.use { it.write(json.toByteArray()) }
                    val response = conn.inputStream.bufferedReader().readText()
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Archivos subidos correctamente", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error al subir archivos", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        // Botón para descargar archivos de la nube
        val btnDownloadFiles = view.findViewById<Button>(R.id.btn_download_routines_cloud)
        btnDownloadFiles.setOnClickListener {
            Thread {
                try {
                    val email = userEmail ?: ""
                    val url = java.net.URL("http://10.0.2.2:3000/api/download-user-files?email=$email")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json")
                    val response = conn.inputStream.bufferedReader().readText()
                    val archivos = Gson().fromJson(response, ArchivoUsuarioResponse::class.java)
                    saveFileContent("bodyweight.json", archivos.ArchivoBody)
                    saveFileContent("rutinas.json", archivos.ArchivoRutina)
                    saveFileContent("ejercicios.json", archivos.ArchivoEjercicio)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Archivos descargados correctamente", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error al descargar archivos", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }
    }

    // Utilidades para leer y guardar archivos locales
    private fun readFileContent(filename: String): String {
        return try {
            requireContext().openFileInput(filename).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
    }

    private fun saveFileContent(filename: String, content: String?) {
        if (content == null) return
        requireContext().openFileOutput(filename, android.content.Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }
    }

    // Clase para mapear la respuesta del backend
    data class ArchivoUsuarioResponse(
        val ArchivoBody: String?,
        val ArchivoRutina: String?,
        val ArchivoEjercicio: String?
    )
}