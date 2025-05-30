package com.example.my_track_fit.fragments

import android.content.Intent // Para cambiar de actividad
import android.os.Bundle // Para manejar el ciclo de vida del fragmento
import android.view.LayoutInflater // Para inflar layouts XML
import android.view.View // Clase base para todos los componentes de UI
import android.view.ViewGroup // Contenedor de vistas
import android.widget.Button // Botón de UI
import android.widget.TextView // Vista para mostrar texto
import androidx.fragment.app.Fragment // Clase base para fragmentos
import android.widget.Toast // Para mostrar mensajes cortos al usuario
import com.example.my_track_fit.LoginActivity // Actividad de login
import com.example.my_track_fit.R // Acceso a recursos (layouts, ids, etc)
import com.google.gson.Gson // Para serializar/deserializar JSON
import androidx.appcompat.app.AlertDialog //dialogos de alerta

class UserSettingsFragment : Fragment() {
    val BASE_URL = "http://192.168.100.153:3000"; // URL base del backend

    // Infla el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_settings, container, false) // Devuelve la vista inflada
    }

    // Se llama después de que la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE) // Preferencias compartidas para sesión
        val userEmail = sharedPref.getString("userEmail", "Correo no disponible") // Obtiene el correo del usuario
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail) // Referencia al TextView del correo
        tvUserEmail.text = userEmail // Muestra el correo en pantalla

        val btnLogout = view.findViewById<Button>(R.id.btn_logout) // Botón para cerrar sesión
        btnLogout.setOnClickListener {
            // Mostrar diálogo de advertencia antes de cerrar sesión
            AlertDialog.Builder(requireContext())
                .setTitle("¿Cerrar sesión?")
                .setMessage("Antes de cerrar sesión, asegúrate de haber cargado tu rutina a la nube.\n\nSi no lo haces, la próxima vez que inicies sesión se cargará automáticamente la rutina que esté en la nube y podrías perder cambios locales.")
                .setPositiveButton("Cerrar sesión") { _, _ ->
                    // Borrar datos de sesión
                    with(sharedPref.edit()) {
                        clear() // Limpia todas las preferencias
                        apply() // Aplica los cambios
                    }
                    // Ir a LoginActivity y cerrar la actividad actual
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpia el stack de actividades
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        val btnChangePassword = view.findViewById<Button>(R.id.btn_change_password) // Botón para cambiar contraseña
        btnChangePassword.setOnClickListener {
            // Mostrar pantalla de carga mientras se realiza la petición
            val progressDialog = android.app.ProgressDialog(requireContext())
            progressDialog.setMessage("Enviando correo de cambio de contraseña...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            // Envía petición al backend para enviar el correo de cambio de contraseña
            val email = userEmail ?: return@setOnClickListener
            Thread {
                try {
                    val url = java.net.URL("$BASE_URL/api/request-password-reset") // URL del endpoint
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST" // Método POST
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    val json = """{"email":"$email"}""" // JSON con el correo
                    conn.outputStream.use { it.write(json.toByteArray()) }
                    val response = conn.inputStream.bufferedReader().readText() // Lee la respuesta
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss() // Oculta la pantalla de carga
                        Toast.makeText(requireContext(), "Correo de cambio de contraseña enviado", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss() // Oculta la pantalla de carga
                        Toast.makeText(requireContext(), "Error enviando correo", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        // Botón para cargar archivos a la nube
        val btnUploadFiles = view.findViewById<Button>(R.id.btn_load_routines_cloud)
        btnUploadFiles.setOnClickListener {
            // Mostrar pantalla de carga mientras se realiza la petición
            val progressDialog = android.app.ProgressDialog(requireContext())
            progressDialog.setMessage("Subiendo archivos a la nube...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            Thread {
                try {
                    val archivoBody = readFileContent("bodyweight.json") // Lee el archivo de peso corporal
                    val archivoRutina = readFileContent("rutinas.json") // Lee el archivo de rutinas
                    val archivoEjercicio = readFileContent("ejercicios.json") // Lee el archivo de ejercicios
                    val email = userEmail ?: ""
                    val json = Gson().toJson(
                        mapOf(
                            "email" to email,
                            "ArchivoBody" to archivoBody,
                            "ArchivoRutina" to archivoRutina,
                            "ArchivoEjercicio" to archivoEjercicio
                        )
                    )
                    val url = java.net.URL("$BASE_URL/api/upload-user-files") // URL del endpoint
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.use { it.write(json.toByteArray()) }
                    val response = conn.inputStream.bufferedReader().readText() // Lee la respuesta
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss() // Oculta la pantalla de carga
                        Toast.makeText(requireContext(), "Archivos subidos correctamente", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss() // Oculta la pantalla de carga
                        Toast.makeText(requireContext(), "Error al subir archivos", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        // Botón para descargar archivos de la nube
        val btnDownloadFiles = view.findViewById<Button>(R.id.btn_download_routines_cloud)
        btnDownloadFiles.setOnClickListener {
            // Crear y mostrar un ProgressDialog para indicar carga
            val progressDialog = android.app.ProgressDialog(requireContext())
            progressDialog.setMessage("Descargando archivos...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            Thread {
                try {
                    val email = userEmail ?: ""
                    val url = java.net.URL("$BASE_URL/api/download-user-files?email=$email") // URL del endpoint con el correo
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json")
                    val response = conn.inputStream.bufferedReader().readText() // Lee la respuesta
                    val archivos = Gson().fromJson(response, ArchivoUsuarioResponse::class.java) // Parsea la respuesta
                    saveFileContent("bodyweight.json", archivos.ArchivoBody) // Guarda el archivo de peso corporal
                    saveFileContent("rutinas.json", archivos.ArchivoRutina) // Guarda el archivo de rutinas
                    saveFileContent("ejercicios.json", archivos.ArchivoEjercicio) // Guarda el archivo de ejercicios
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss() // Oculta la pantalla de carga
                        Toast.makeText(requireContext(), "Archivos descargados correctamente", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        progressDialog.dismiss() // Oculta la pantalla de carga
                        Toast.makeText(requireContext(), "Error al descargar archivos", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        // Botón para eliminar la cuenta del usuario
        val btnDeleteAccount = view.findViewById<Button>(R.id.btn_delete_account)
        btnDeleteAccount.setOnClickListener {
            // Muestra un diálogo de confirmación antes de eliminar la cuenta
            val dialog = AlertDialog.Builder(requireContext())
            .setTitle("¿Eliminar cuenta?")
            .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                // Si el usuario confirma, procede a eliminar la cuenta
                val email = userEmail ?: return@setPositiveButton
                // Muestra un ProgressDialog mientras se realiza la petición
                val progressDialog = android.app.ProgressDialog(requireContext())
                progressDialog.setMessage("Eliminando cuenta...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                Thread {
                try {
                    // Realiza una petición POST al backend para eliminar la cuenta
                    val url = java.net.URL("$BASE_URL/api/delete-account")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    val json = """{"email":"$email"}"""
                    conn.outputStream.use { it.write(json.toByteArray()) }
                    val response = conn.inputStream.bufferedReader().readText()
                    requireActivity().runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Cuenta eliminada", Toast.LENGTH_LONG).show()
                    // Borra los datos de sesión y regresa a la pantalla de login
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                    }
                } catch (e: Exception) {
                    // Si ocurre un error, muestra un mensaje al usuario
                    requireActivity().runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error al eliminar la cuenta", Toast.LENGTH_LONG).show()
                    }
                }
                }.start()
            }
            .setNegativeButton("Cancelar", null) // Si cancela, no hace nada
            .create()
            
            dialog.show() // Muestra el diálogo de confirmación
        }
    }

    // Utilidad para leer el contenido de un archivo local
    private fun readFileContent(filename: String): String {
        return try {
            requireContext().openFileInput(filename).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
    }

    // Utilidad para guardar contenido en un archivo local
    private fun saveFileContent(filename: String, content: String?) {
        if (content == null) return
        requireContext().openFileOutput(filename, android.content.Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }
    }

    // Clase para mapear la respuesta del backend al descargar archivos
    data class ArchivoUsuarioResponse(
        val ArchivoBody: String?,
        val ArchivoRutina: String?,
        val ArchivoEjercicio: String?
    )
}