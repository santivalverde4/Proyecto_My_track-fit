package com.example.my_track_fit.model

class Week(
    //Attributes / constructor
    //val id: Int = 0, // Data base assigns automatically the id
    private var exerciseInstanceList: MutableList<ExerciseInstance>,
    private var block: Block
) {
    // Getters & setters
    //---exerciseInstanceList
    fun getExerciseInstanceList(): MutableList<ExerciseInstance> {
        return exerciseInstanceList
    }

    fun setExerciseInstanceList(exerciseInstanceList: MutableList<ExerciseInstance>) {
        this.exerciseInstanceList = exerciseInstanceList
    }

    //---block
    fun getBlock(): Block {
        return block
    }

    fun setBlock(block: Block) {
        this.block = block
    }

    //Methods
    fun addExerciseInstance(exercise: Exercise) {
        // Create new ExerciseInstance object
        val newExerciseInstance = ExerciseInstance(
            week = this,
            exercise = exercise,
            setsData = mutableMapOf() //initialize the dictionary
        )
        exerciseInstanceList.add(newExerciseInstance)
    }

    //---deleteExerciseInstance
    fun deleteExerciseInstance(exerciseInstance: ExerciseInstance) {
        exerciseInstanceList.remove(exerciseInstance)
    }
}