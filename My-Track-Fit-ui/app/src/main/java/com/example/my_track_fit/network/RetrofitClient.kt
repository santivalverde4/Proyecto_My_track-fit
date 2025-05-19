package com.example.my_track_fit.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import com.example.my_track_fit.model.Routine
import com.example.my_track_fit.model.Exercise

// MODELOS
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

data class ExercisesResponse(
    val exercises: List<Exercise>
)

data class UpdateExerciseRequest(val name: String)

data class Bodyweight(val id: Int, val peso: Int, val fecha: String)
data class CreateBodyweightRequest(val userId: Int, val peso: Int)
data class UpdateBodyweightRequest(val userId: Int, val peso: Int)
data class DeleteBodyweightRequest(val userId: Int)

data class ForgotPasswordRequest(val Email: String)
data class ForgotPasswordResponse(val success: Boolean, val message: String)


// UpdateProfileRequest.kt
data class UpdateProfileRequest(val Id: Int, val Username: String, val Email: String, val Password: String)

// UpdateProfileResponse.kt
data class UpdateProfileResponse(val success: Boolean, val message: String)

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

    @PUT("update-profile")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<UpdateProfileResponse>
    fun signUpUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("exercise")
    fun addExercise(@Body request: AddExerciseRequest): Call<ExerciseResponse>

    @GET("workout/{id}")
    fun getWorkout(@Path("id") id: Int): Call<WorkoutResponse>

    @GET("workout/{id}/exercises")
    fun getExercises(@Path("id") id: Int): Call<ExercisesResponse>

    @PUT("exercise/{id}")
    fun updateExercise(@Path("id") id: Int, @Body request: UpdateExerciseRequest): Call<ExerciseResponse>

    @DELETE("exercise/{id}")
    fun deleteExercise(@Path("id") id: Int): Call<ExerciseResponse>

}

// SINGLETON RETROFIT
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