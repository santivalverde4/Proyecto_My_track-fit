package com.example.my_track_fit.model

import java.io.Serializable

class Routine (
    //val id: Int = 0, // Data base assigns automatically the id
    private var name: String,
    private var weekList: MutableList<Week> = mutableListOf(), // Se inicializa por defecto
    @Transient private var workout: Workout
) : Serializable { 
    init {
        // Si la lista está vacía, agrega una semana inicial
        if (weekList.isEmpty()) {
            weekList.add(Week(
                blockList = mutableListOf(),
                routine = this
            ))
        }
    }
    //Getters & setters
    //---name
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String {
        return name
    }
    //---workout
    fun setWorkout(workOut: Workout){
        this.workout = workOut
    }
    fun getWorkout(): Workout {
        return workout
    }
    //---weeks
    fun setWeeks(weekList: MutableList<Week>){
        this.weekList = weekList
    }
    fun getWeeks(): MutableList<Week> {
        return weekList
    }

    //Methods
    fun addWeek() {
        val newWeek = Week(
            blockList = mutableListOf(),
            routine = this
        )
        weekList.add(newWeek)
    }

    // Copia la semana completa (con datos)
    fun copyWeekData(week: Week): Week {
        val newWeek = Week(
            blockList = mutableListOf(),
            routine = this
        )

        val newBlockList = week.getBlockList().map { block ->
            val newExerciseInstances = block.getExerciseInstanceList().map { exerciseInstance ->
                ExerciseInstance(
                    block = block, // Si necesitas que apunte al nuevo bloque, ajusta después
                    exercise = exerciseInstance.getExercise(),
                    setsData = exerciseInstance.getSetsData().mapValues { (_, setData) ->
                        ExerciseInstance.SetData(setData.weight, setData.reps, setData.rpe)
                    }.toMutableMap()
                )
            }.toMutableList()
            Block(
                name = block.getName(),
                exerciseInstanceList = newExerciseInstances,
                week = newWeek
            )
        }.toMutableList()

        newWeek.setBlockList(newBlockList)
        return newWeek
    }

    // Copia la semana pero los ExerciseInstance tendrán setsData vacío
    fun copyWeekNoData(week: Week): Week {
        val newWeek = Week(
            blockList = mutableListOf(),
            routine = this
        )

        val newBlockList = week.getBlockList().map { block ->
            val newExerciseInstances = block.getExerciseInstanceList().map { exerciseInstance ->
                ExerciseInstance(
                    block = block, // Si necesitas que apunte al nuevo bloque, ajusta después
                    exercise = exerciseInstance.getExercise(),
                    setsData = mutableMapOf()
                )
            }.toMutableList()
            Block(
                name = block.getName(),
                exerciseInstanceList = newExerciseInstances,
                week = newWeek
            )
        }.toMutableList()

        newWeek.setBlockList(newBlockList)
        return newWeek
    }

    fun deleteWeek(week: Week) {
        weekList.remove(week)
    }
}