package com.example.my_track_fit

import android.content.Intent // Permite cambiar de actividad
import android.os.Bundle // Para manejar el ciclo de vida de la actividad
import android.widget.Button // Botón de UI
import android.widget.EditText // Campo de texto editable
import android.widget.TextView // Vista para mostrar texto
import android.widget.Toast // Para mostrar mensajes cortos al usuario
import androidx.appcompat.app.AppCompatActivity // Actividad base para compatibilidad
import com.example.my_track_fit.network.LoginRequest // Modelo para petición de login
import com.example.my_track_fit.network.LoginResponse // Modelo para respuesta de login
import com.example.my_track_fit.network.RetrofitClient // Cliente Retrofit para llamadas HTTP
import retrofit2.Call // Llamada HTTP
import retrofit2.Callback // Callback para respuesta HTTP
import retrofit2.Response // Respuesta HTTP

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val BASE_URL = "http://10.0.2.2:3000"; // URL base del backend
        super.onCreate(savedInstanceState)
        // Verificar si ya hay sesión iniciada
        val sharedPref = getSharedPreferences("MyTrackFitPrefs", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login) // Establece el layout de la actividad

        // Vincular los elementos de la interfaz
        val usernameEditText = findViewById<EditText>(R.id.etMail) // Campo de correo
        val passwordEditText = findViewById<EditText>(R.id.etPassword) // Campo de contraseña
        val loginButton = findViewById<Button>(R.id.btnLogin) // Botón de login
        val signUpTextView = findViewById<TextView>(R.id.tvGoToSignUp) // Enlace para registro

        val forgotPasswordTextView = findViewById<TextView>(R.id.tvForgotPassword) // Enlace para recuperar contraseña
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
                                val url = java.net.URL("$BASE_URL/api/request-password-reset") // Endpoint para recuperar contraseña
                                val conn = url.openConnection() as java.net.HttpURLConnection
                                conn.requestMethod = "POST"
                                conn.setRequestProperty("Content-Type", "application/json")
                                conn.doOutput = true
                                val json = """{"email":"$email"}""" // JSON con el correo
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

    // Método para iniciar sesión con el backend usando Retrofit
    private fun loginUser(username: String, password: String) {
        val apiService = RetrofitClient.instance // Obtiene la instancia del servicio API
        val loginRequest = LoginRequest(username, password) // Crea el objeto de petición

        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            // Se ejecuta cuando se recibe una respuesta del servidor
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val sharedPref = getSharedPreferences("MyTrackFitPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("userId", response.body()?.Id ?: -1) // Guarda el id del usuario
                        putBoolean("isLoggedIn", true) // Marca la sesión como iniciada
                        putString("userEmail", username) // Guarda el correo del usuario
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

            // Se ejecuta si ocurre un error de red o no hay respuesta
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}