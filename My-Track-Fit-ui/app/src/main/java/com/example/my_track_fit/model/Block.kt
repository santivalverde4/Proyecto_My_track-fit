package com.example.my_track_fit.model

class Block (
    //Attributes / constructor
    //val id: Int = 0, // Data base assigns automatically the id
    private var name: String,
    private var exerciseInstanceList: MutableList<ExerciseInstance>,
    private var week: Week
){
    //Setters & getters
    //---name
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String {
        return name
    }
    //---exerciseInstanceList
    fun getExerciseInstanceList(): MutableList<ExerciseInstance> {
        return exerciseInstanceList
    }

    fun setExerciseInstanceList(exerciseInstanceList: MutableList<ExerciseInstance>) {
        this.exerciseInstanceList = exerciseInstanceList
    }

    //---week
    fun setWeek(week: Week){
        this.week = week
    }
    fun getWeek(): Week {
        return week
    }

    //Methods
    fun addExerciseInstance(exercise: Exercise) {
        // Crear un nuevo ExerciseInstance usando el bloque actual y el ejercicio dado
        val newExerciseInstance = ExerciseInstance(
            block = this,
            exercise = exercise,
            setsData = mutableMapOf() // Inicializa el diccionario vacío
        )
        exerciseInstanceList.add(newExerciseInstance)
    }

    //---deleteExerciseInstance
    fun deleteExerciseInstance(exerciseInstance: ExerciseInstance) {
        exerciseInstanceList.remove(exerciseInstance)
    }
}