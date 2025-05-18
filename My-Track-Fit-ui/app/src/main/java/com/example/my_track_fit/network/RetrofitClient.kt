package com.example.my_track_fit.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// MODELOS
data class LoginRequest(val Username: String, val Password: String)
data class SignUpRequest(val Username: String, val Password: String, val Email: String)
data class LoginResponse(val success: Boolean, val message: String, val Id: Int?)

data class Bodyweight(val id: Int, val peso: Int, val fecha: String)
data class CreateBodyweightRequest(val userId: Int, val peso: Int)
data class UpdateBodyweightRequest(val userId: Int, val peso: Int)
data class DeleteBodyweightRequest(val userId: Int)

data class ForgotPasswordRequest(val Email: String)
data class ForgotPasswordResponse(val success: Boolean, val message: String)

// API SERVICE
interface ApiService {
    // Auth
    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("signup")
    fun signUpUser(@Body request: SignUpRequest): Call<LoginResponse>

    // Bodyweight
    @GET("bodyweight/{userId}")
    fun getBodyweights(@Path("userId") userId: Int): Call<List<Bodyweight>>

    @POST("bodyweight")
    fun createBodyweight(@Body request: CreateBodyweightRequest): Call<Void>

    @PUT("bodyweight/{id}")
    fun updateBodyweight(@Path("id") id: Int, @Body request: UpdateBodyweightRequest): Call<Void>

    @HTTP(method = "DELETE", path = "bodyweight/{id}", hasBody = true)
    fun deleteBodyweight(@Path("id") id: Int, @Body request: DeleteBodyweightRequest): Call<Void>

    @POST("forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>
}

// SINGLETON RETROFIT
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