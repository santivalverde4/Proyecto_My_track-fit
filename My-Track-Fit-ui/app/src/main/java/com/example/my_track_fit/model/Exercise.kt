package com.example.my_track_fit.model

import java.io.Serializable // Permite que la clase se pueda serializar (guardar/cargar en archivos o transferir entre componentes)

// Clase que representa un ejercicio
class Exercise (
    // Atributos / constructor
    val id: Int = 0, // El id lo asigna automáticamente la base de datos
    private var name: String, // Nombre del ejercicio
    @Transient private var workout: Workout // Referencia al workout al que pertenece (no se serializa)
) : Serializable { 
    // Getters y setters

    //---name
    fun setName(name: String){
        this.name = name // Cambia el nombre del ejercicio
    }
    fun getName(): String {
        return name // Devuelve el nombre del ejercicio
    }

    //---workOut
    fun setWorkout(workOut: Workout){
        this.workout = workOut // Cambia la referencia al workout
    }
    fun getWorkout(): Workout {
        return workout // Devuelve la referencia al workout
    }
}