package com.example.my_track_fit.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginRequest(val Username: String, val Password: String)

// Define la respuesta que se recibe del backend
data class LoginResponse(val success: Boolean, val message: String, val Id: Int?)

// Define las rutas del API
interface ApiService {
    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("signup")
    fun signUpUser(@Body request: LoginRequest): Call<LoginResponse>
}

// Singleton para Retrofit
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}