package com.example.my_track_fit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_track_fit.network.LoginRequest
import com.example.my_track_fit.network.LoginResponse
import com.example.my_track_fit.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val BASE_URL = "http://192.168.100.153:3000"; 
        super.onCreate(savedInstanceState)
        // Verificar si ya hay sesión iniciada
        val sharedPref = getSharedPreferences("MyTrackFitPrefs", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Vincular los elementos de la interfaz
        val usernameEditText = findViewById<EditText>(R.id.etMail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpTextView = findViewById<TextView>(R.id.tvGoToSignUp) // Vincular el TextView para registro

        val forgotPasswordTextView = findViewById<TextView>(R.id.tvForgotPassword)
        forgotPasswordTextView.setOnClickListener {
            val input = EditText(this)
            input.hint = "Correo electrónico"
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Recuperar contraseña")
                .setMessage("Ingresa tu correo para recibir el enlace de recuperación.")
                .setView(input)
                .setPositiveButton("Enviar") { _, _ ->
                    val email = input.text.toString().trim()
                    if (email.isNotEmpty()) {
                        Thread {
                            try {
                                val url = java.net.URL("$BASE_URL/api/request-password-reset")
                                val conn = url.openConnection() as java.net.HttpURLConnection
                                conn.requestMethod = "POST"
                                conn.setRequestProperty("Content-Type", "application/json")
                                conn.doOutput = true
                                val json = """{"email":"$email"}"""
                                conn.outputStream.use { it.write(json.toByteArray()) }
                                val response = conn.inputStream.bufferedReader().readText()
                                runOnUiThread {
                                    Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(this, "Error enviando correo", Toast.LENGTH_LONG).show()
                                }
                            }
                        }.start()
                    } 
                    else {
                        Toast.makeText(this, "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
}
        // Configurar el botón de inicio de sesión
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar los datos ingresados
            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password) // Llamar al método para enviar los datos al backend
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el enlace para ir a la pantalla de registro
        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        val apiService = RetrofitClient.instance
        val loginRequest = LoginRequest(username, password)

        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val sharedPref = getSharedPreferences("MyTrackFitPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("userId", response.body()?.Id ?: -1)
                        putBoolean("isLoggedIn", true)
                        putString("userEmail", username) // <-- Guarda el correo
                        apply()
                    }
                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this@LoginActivity, "Error: "+(response.body()?.message ?: "Error desconocido"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
               Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}