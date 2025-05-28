package com.example.my_track_fit.model

import java.io.Serializable // Permite que la clase se pueda serializar (guardar/cargar en archivos o transferir entre componentes)

// Clase que representa el conjunto de rutinas y ejercicios del usuario
class Workout(
    //Attributes / constructor
    val id: Int = 0, // El id lo asigna automáticamente la base de datos
    private var routineList: MutableList<Routine>, // Lista de rutinas del usuario
    private var exerciseList: MutableList<Exercise> // Lista de ejercicios del usuario
) : Serializable { 

    // Segundo constructor sin parámetros, inicializa listas vacías
    constructor() : this(
        0,
        mutableListOf<Routine>(),
        mutableListOf<Exercise>()
    )

    // Métodos

    // Agrega una nueva rutina con el nombre dado
    fun addRoutine(routineName: String) {
        val newRoutine = Routine(
            name = routineName, // Asigna el nombre de la rutina
            weekList = mutableListOf(), // Inicializa la lista de semanas vacía
            workout = this // Asocia la rutina a este workout
        )
        routineList.add(newRoutine) // Agrega la rutina a la lista
    }

    // Elimina una rutina de la lista
    fun deleteRoutine(routine: Routine) {
        routineList.remove(routine) // Quita la rutina de la lista
    }

    // Agrega un nuevo ejercicio con el nombre dado
    fun addExercise(exerciseName: String) {
        // Crear un nuevo objeto Exercise
        val newExercise = Exercise(
            name = exerciseName, // Asignar el nombre pasado por parámetro
            workout = this // Asignar el WorkOut actual
        )
        // Agregar el nuevo Exercise a la lista
        exerciseList.add(newExercise)
    }

    // Elimina un ejercicio de la lista
    fun deleteExercise(exercise: Exercise) {
        exerciseList.remove(exercise) // Quita el ejercicio de la lista
    }

    // Getters & setters

    //---routines
    fun setRoutines(routineList: MutableList<Routine>) {
        this.routineList = routineList // Cambia la lista de rutinas
    }
    fun getRoutines(): MutableList<Routine> {
        return routineList // Devuelve la lista de rutinas
    }

    //---Exercise
    fun setExercise(exerciseList: MutableList<Exercise>) {
        this.exerciseList = exerciseList // Cambia la lista de ejercicios
    }
    fun getExercise(): MutableList<Exercise> {
        return exerciseList // Devuelve la lista de ejercicios
    }

    // Permite establecer la lista de ejercicios desde una lista externa (por ejemplo, al cargar desde archivo)
    fun setExercises(ejercicios: List<Exercise>) {
        this.exerciseList.clear() // Limpia la lista actual
        this.exerciseList.addAll(ejercicios) // Agrega todos los ejercicios de la lista recibida
    }
}