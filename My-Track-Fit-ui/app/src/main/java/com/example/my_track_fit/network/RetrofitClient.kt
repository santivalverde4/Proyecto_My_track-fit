package com.example.my_track_fit.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.my_track_fit.model.Routine
import com.example.my_track_fit.model.Exercise

data class LoginRequest(val Username: String, val Password: String)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val Id: Int?,
    val workoutId: Int?
)

data class ExerciseResponse(val success: Boolean, val exercise: Exercise?)

data class AddExerciseRequest(
    val name: String,
    val workoutId: Int
)

data class WorkoutResponse(
    val id: Int,
    val routines: List<Routine>,
    val exercises: List<Exercise>
)

// Define las rutas del API
interface ApiService {
    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("signup")
    fun signUpUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("exercise")
    fun addExercise(@Body request: AddExerciseRequest): Call<ExerciseResponse>

    @GET("workout/{id}")
    fun getWorkout(@Path("id") id: Int): Call<WorkoutResponse>
}

// Singleton para Retrofit
object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.153:3000/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}