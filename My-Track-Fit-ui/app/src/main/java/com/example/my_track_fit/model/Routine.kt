package com.example.my_track_fit.model

import java.io.Serializable // Permite que la clase se pueda serializar (guardar/cargar en archivos o transferir entre componentes)

// Clase que representa una rutina de entrenamiento
class Routine (
    //val id: Int = 0, // Data base assigns automatically the id
    private var name: String, // Nombre de la rutina
    private var weekList: MutableList<Week> = mutableListOf(), // Lista de semanas de la rutina (inicializada vacía por defecto)
    @Transient private var workout: Workout // Referencia al workout al que pertenece (no se serializa)
) : Serializable { 
    init {
        // Si la lista de semanas está vacía, agrega una semana inicial por defecto
        if (weekList.isEmpty()) {
            weekList.add(Week(
                blockList = mutableListOf(), // Lista vacía de bloques
                routine = this // Asocia la semana a esta rutina
            ))
        }
    }
    // Getters & setters

    //---name
    fun setName(name: String){
        this.name = name // Cambia el nombre de la rutina
    }
    fun getName(): String {
        return name // Devuelve el nombre de la rutina
    }

    //---workout
    fun setWorkout(workOut: Workout){
        this.workout = workOut // Cambia la referencia al workout
    }
    fun getWorkout(): Workout {
        return workout // Devuelve la referencia al workout
    }

    //---weeks
    fun setWeeks(weekList: MutableList<Week>){
        this.weekList = weekList // Cambia la lista de semanas
    }
    fun getWeeks(): MutableList<Week> {
        return weekList // Devuelve la lista de semanas
    }

    // Métodos

    // Agrega una nueva semana vacía a la rutina
    fun addWeek() {
        val newWeek = Week(
            blockList = mutableListOf(), // Nueva semana con lista vacía de bloques
            routine = this // Asocia la semana a esta rutina
        )
        weekList.add(newWeek) // Agrega la nueva semana a la lista
    }

    // Copia la semana completa (con datos de sets)
    fun copyWeekData(week: Week): Week {
        val newWeek = Week(
            blockList = mutableListOf(), // Nueva semana con lista vacía de bloques
            routine = this // Asocia la semana a esta rutina
        )

        // Crea una nueva lista de bloques copiando cada bloque y sus instancias de ejercicio (con setsData)
        val newBlockList = week.getBlockList().map { block ->
            val newExerciseInstances = block.getExerciseInstanceList().map { exerciseInstance ->
                ExerciseInstance(
                    block = block, // Asocia la instancia al bloque original (puedes ajustar si necesitas que apunte al nuevo bloque)
                    exercise = exerciseInstance.getExercise(),
                    setsData = exerciseInstance.getSetsData().mapValues { (_, setData) ->
                        ExerciseInstance.SetData(setData.weight, setData.reps, setData.rpe) // Copia los datos del set
                    }.toMutableMap()
                )
            }.toMutableList()
            Block(
                name = block.getName(),
                exerciseInstanceList = newExerciseInstances,
                week = newWeek // Asocia el bloque a la nueva semana
            )
        }.toMutableList()

        newWeek.setBlockList(newBlockList) // Asigna la lista de bloques a la nueva semana
        return newWeek // Devuelve la nueva semana copiada
    }

    // Copia la semana pero los ExerciseInstance tendrán setsData vacío
    fun copyWeekNoData(week: Week): Week {
        val newWeek = Week(
            blockList = mutableListOf(), // Nueva semana con lista vacía de bloques
            routine = this // Asocia la semana a esta rutina
        )

        // Crea una nueva lista de bloques copiando solo la estructura, sin datos de sets
        val newBlockList = week.getBlockList().map { block ->
            val newExerciseInstances = block.getExerciseInstanceList().map { exerciseInstance ->
                ExerciseInstance(
                    block = block, // Asocia la instancia al bloque original (puedes ajustar si necesitas que apunte al nuevo bloque)
                    exercise = exerciseInstance.getExercise(),
                    setsData = mutableMapOf() // No copia los datos de sets
                )
            }.toMutableList()
            Block(
                name = block.getName(),
                exerciseInstanceList = newExerciseInstances,
                week = newWeek // Asocia el bloque a la nueva semana
            )
        }.toMutableList()

        newWeek.setBlockList(newBlockList) // Asigna la lista de bloques a la nueva semana
        return newWeek // Devuelve la nueva semana copiada
    }

    // Elimina una semana de la rutina
    fun deleteWeek(week: Week) {
        weekList.remove(week) // Quita la semana de la lista
    }
}