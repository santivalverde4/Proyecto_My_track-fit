package com.example.my_track_fit.network

import retrofit2.Call // Permite realizar llamadas HTTP asíncronas o síncronas
import retrofit2.Retrofit // Clase principal para crear instancias de Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Convierte JSON a objetos Kotlin y viceversa
import retrofit2.http.Body // Anotación para indicar el cuerpo de la petición
import retrofit2.http.POST // Anotación para peticiones POST

// Data class para enviar los datos de login o registro al backend
data class LoginRequest(val Username: String, val Password: String)

// Data class que representa la respuesta del backend al hacer login o registro
data class LoginResponse(val success: Boolean, val message: String, val Id: Int?)

// Interfaz que define las rutas del API y los métodos HTTP
interface ApiService {
    @POST("login") // Indica que este método hace una petición POST a /login
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse> // Envía los datos de login y espera una respuesta

    @POST("signup") // Indica que este método hace una petición POST a /signup
    fun signUpUser(@Body request: LoginRequest): Call<LoginResponse> // Envía los datos de registro y espera una respuesta
}

// Objeto singleton que configura y expone la instancia de Retrofit
object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.153:3000/api/" // URL base del backend

    // Instancia única de ApiService, inicializada solo una vez (lazy)
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Establece la URL base
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para convertir JSON
            .build() // Construye la instancia de Retrofit
            .create(ApiService::class.java) // Crea la implementación de la interfaz ApiService
    }
}