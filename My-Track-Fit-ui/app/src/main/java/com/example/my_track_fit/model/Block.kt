package com.example.my_track_fit.model

import java.io.Serializable // Permite que la clase se pueda serializar (guardar/cargar en archivos o transferir entre componentes)

// Clase que representa un bloque dentro de una semana de rutina
class Block (
    // Atributos del bloque
    //val id: Int = 0, // El id lo asigna automáticamente la base de datos (comentado)
    private var name: String, // Nombre del bloque
    private var exerciseInstanceList: MutableList<ExerciseInstance>, // Lista de instancias de ejercicios en el bloque
    @Transient private var week: Week // Semana a la que pertenece el bloque (no se serializa)
) : Serializable { 
    
    // Setters y getters para los atributos

    //---name
    fun setName(name: String){
        this.name = name // Cambia el nombre del bloque
    }
    fun getName(): String {
        return name // Devuelve el nombre del bloque
    }

    //---exerciseInstanceList
    fun getExerciseInstanceList(): MutableList<ExerciseInstance> {
        return exerciseInstanceList // Devuelve la lista de instancias de ejercicios
    }

    fun setExerciseInstanceList(exerciseInstanceList: MutableList<ExerciseInstance>) {
        this.exerciseInstanceList = exerciseInstanceList // Cambia la lista de instancias de ejercicios
    }

    //---week
    fun setWeek(week: Week){
        this.week = week // Cambia la semana a la que pertenece el bloque
    }
    fun getWeek(): Week {
        return week // Devuelve la semana a la que pertenece el bloque
    }

    // Métodos

    // Agrega una nueva instancia de ejercicio al bloque
    fun addExerciseInstance(exercise: Exercise) {
        // Crear un nuevo ExerciseInstance usando el bloque actual y el ejercicio dado
        val newExerciseInstance = ExerciseInstance(
            block = this, // Asocia la instancia al bloque actual
            exercise = exercise, // Asocia la instancia al ejercicio dado
            setsData = mutableMapOf() // Inicializa el diccionario vacío de sets
        )
        exerciseInstanceList.add(newExerciseInstance) // Agrega la nueva instancia a la lista
    }

    //---deleteExerciseInstance
    // Elimina una instancia de ejercicio del bloque
    fun deleteExerciseInstance(exerciseInstance: ExerciseInstance) {
        exerciseInstanceList.remove(exerciseInstance) // Quita la instancia de la lista
    }
}