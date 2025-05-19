package com.example.my_track_fit

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_track_fit.network.ApiService
import com.example.my_track_fit.network.LoginRequest
import com.example.my_track_fit.network.LoginResponse   // <-- AGREGA ESTA LÍNEA
import com.example.my_track_fit.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val usernameEditText = findViewById<EditText>(R.id.etSignUpMail)
        val passwordEditText = findViewById<EditText>(R.id.etSignUpPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.etConfirmPassword)
        val signUpButton = findViewById<Button>(R.id.btnSignUp)
        val goToLoginTextView = findViewById<TextView>(R.id.tvGoToLogin) 

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                signUpUser(username, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
        // Agrega este bloque para cambiar a LoginActivity
        goToLoginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser(username: String, password: String) {
        val apiService = RetrofitClient.instance
        val signUpRequest = LoginRequest(username, password)

        apiService.signUpUser(signUpRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.i("SignUpDebug", "Registro exitoso: ${response.body()}")
                    Toast.makeText(this@SignUpActivity, "Registro exitoso. Revisa tu correo para confirmar tu cuenta.", Toast.LENGTH_LONG).show()
                    finish() // Regresar a la pantalla de login
                } else {
                    Log.e("SignUpError", "Error en registro: ${response.body()?.message}")
                    Toast.makeText(this@SignUpActivity, "Error: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
}

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("SignUpError", "Error de conexión", t)
                Toast.makeText(this@SignUpActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}