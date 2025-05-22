package com.example.my_track_fit.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart

data class LoginRequest(val Username: String, val Password: String)

// Define la respuesta que se recibe del backend
data class LoginResponse(val success: Boolean, val message: String, val Id: Int?)

// Define las rutas del API
interface ApiService {
    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("signup")
    fun signUpUser(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("archivo/upload")
    fun uploadArchivo(
        @Part archivo: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("tipo") tipo: RequestBody
    ): Call<LoginResponse>

    @GET("archivo/download/{userId}/{tipo}")
    fun downloadArchivo(
        @Path("userId") userId: Int,
        @Path("tipo") tipo: String
    ): Call<ResponseBody>
}

// Singleton para Retrofit
object RetrofitClient {
    private const val BASE_URL = "http:///192.168.0.9:3000/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}