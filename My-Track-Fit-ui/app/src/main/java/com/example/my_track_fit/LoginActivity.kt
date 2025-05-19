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
import com.example.my_track_fit.network.ForgotPasswordRequest
import com.example.my_track_fit.network.ForgotPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageButton
import com.example.my_track_fit.UserSession

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
        val signUpTextView = findViewById<TextView>(R.id.tvGoToSignUp)
        val forgotPasswordTextView = findViewById<TextView>(R.id.tvForgotPassword)

        // Botón de inicio de sesión
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Enlace para ir a la pantalla de registro
        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Enlace para recuperar contraseña
        forgotPasswordTextView.setOnClickListener {
            showForgotPasswordDialog()
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
                        putString("userEmail", username)
                        putInt("workoutId", response.body()?.workoutId ?: -1) // <-- Guarda el workoutId
                        apply()
                    }
                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Guarda el userId usando UserSession
                    val userId = response.body()?.Id ?: -1
                    UserSession.saveUserId(this@LoginActivity, userId)
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

    private fun showForgotPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password_dialog, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.etForgotEmail)
        val sendButton = dialogView.findViewById<Button>(R.id.btnSendForgot)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.btnClose)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        closeButton.setOnClickListener { dialog.dismiss() }

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                sendForgotPasswordRequest(email, dialog)
            } else {
                Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun sendForgotPasswordRequest(email: String, dialog: AlertDialog) {
        val request = ForgotPasswordRequest(email)
        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(
                call: Call<ForgotPasswordResponse>,
                response: Response<ForgotPasswordResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@LoginActivity, "Correo enviado. Revisa tu bandeja.", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@LoginActivity, response.body()?.message ?: "Correo no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}