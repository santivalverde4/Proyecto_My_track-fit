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

class UserSettingsFragment : Fragment() {
    private val client = OkHttpClient()
    private val baseUrl = "http://192.168.0.153:3000" 

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
                    val url = java.net.URL("http://192.168.100.153:3000/api/request-password-reset")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    val json = """{"email":"$email"}"""
                    conn.outputStream.use { it.write(json.toByteArray()) }
                    val response = conn.inputStream.bufferedReader().readText()
                    requireActivity().runOnUiThread {
                        android.widget.Toast.makeText(requireContext(), "Correo de cambio de contraseña enviado", android.widget.Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        android.widget.Toast.makeText(requireContext(), "Error enviando correo", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }
    }
}