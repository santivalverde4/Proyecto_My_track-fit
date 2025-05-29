package com.example.my_track_fit

import android.util.Log // Para imprimir mensajes en la consola de depuración
import android.content.Intent // Permite cambiar de actividad
import android.os.Bundle // Para manejar el ciclo de vida de la actividad
import android.widget.Button // Botón de UI
import android.widget.EditText // Campo de texto editable
import android.widget.TextView // Vista para mostrar texto
import android.widget.Toast // Para mostrar mensajes cortos al usuario
import androidx.appcompat.app.AppCompatActivity // Actividad base para compatibilidad
import com.example.my_track_fit.network.ApiService // Interfaz de la API para Retrofit
import com.example.my_track_fit.network.LoginRequest // Modelo para petición de registro/login
import com.example.my_track_fit.network.LoginResponse // Modelo para respuesta de registro/login
import com.example.my_track_fit.network.RetrofitClient // Cliente Retrofit para llamadas HTTP
import retrofit2.Call // Llamada HTTP
import retrofit2.Callback // Callback para respuesta HTTP
import retrofit2.Response // Respuesta HTTP

class SignUpActivity : AppCompatActivity() {
    // Función para validar el formato del correo electrónico
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.matches(emailRegex) // Devuelve true si el correo es válido
    }

    // Función para validar la seguridad de la contraseña
    private fun isValidPassword(password: String): Boolean {
        // Mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&._-])[A-Za-z\\d@\$!%*?&._-]{8,}$".toRegex()
        return password.matches(passwordRegex) // Devuelve true si la contraseña es segura
    }

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup) // Establece el layout de la actividad

        val usernameEditText = findViewById<EditText>(R.id.etSignUpMail) // Campo de correo
        val passwordEditText = findViewById<EditText>(R.id.etSignUpPassword) // Campo de contraseña
        val confirmPasswordEditText = findViewById<EditText>(R.id.etConfirmPassword) // Campo para confirmar contraseña
        val signUpButton = findViewById<Button>(R.id.btnSignUp) // Botón de registro
        val goToLoginTextView = findViewById<TextView>(R.id.tvGoToLogin) // Enlace para ir a login

        // Listener para el botón de registro
        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validaciones de los campos
            when {
                !isValidEmail(username) -> {
                    // Muestra un diálogo si el correo no es válido
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Correo no válido")
                        .setMessage("Por favor ingresa un correo electrónico válido.")
                        .setPositiveButton("OK", null)
                        .show()
                }
                !isValidPassword(password) -> {
                    // Muestra un diálogo si la contraseña es insegura
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Contraseña insegura")
                        .setMessage("Debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.")
                        .setPositiveButton("OK", null)
                        .show()
                }
                password != confirmPassword -> {
                    // Muestra un diálogo si las contraseñas no coinciden
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Contraseñas no coinciden")
                        .setMessage("Las contraseñas ingresadas no son iguales.")
                        .setPositiveButton("OK", null)
                        .show()
                }
                else -> {
                    // Si todo es válido, intenta registrar al usuario
                    signUpUser(username, password)
                }
            }
        }
        // Listener para cambiar a LoginActivity al hacer clic en el texto
        goToLoginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Función para registrar al usuario usando Retrofit
    private fun signUpUser(username: String, password: String) {
        val apiService = RetrofitClient.instance // Obtiene la instancia del servicio API
        val signUpRequest = LoginRequest(username, password) // Crea el objeto de petición

        apiService.signUpUser(signUpRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
            // Se ejecuta cuando se recibe una respuesta del servidor
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.i("SignUpDebug", "Registro exitoso: ${response.body()}") // Log de éxito
                    Toast.makeText(this@SignUpActivity, "Registro exitoso. Revisa tu correo para confirmar tu cuenta.", Toast.LENGTH_LONG).show()
                    finish() // Regresa a la pantalla de login
                } else {
                    Log.e("SignUpError", "Error en registro: ${response.body()?.message}") // Log de error
                    Toast.makeText(this@SignUpActivity, "Error: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Se ejecuta si ocurre un error de red o no hay respuesta
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("SignUpError", "Error de conexión", t) // Log de error de conexión
                Toast.makeText(this@SignUpActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}