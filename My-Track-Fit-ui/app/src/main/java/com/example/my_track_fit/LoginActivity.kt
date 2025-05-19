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
                    // Guarda el ID del usuario o un flag de sesión
                    val sharedPref = getSharedPreferences("MyTrackFitPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("userId", response.body()?.Id ?: -1)
                        putBoolean("isLoggedIn", true)
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