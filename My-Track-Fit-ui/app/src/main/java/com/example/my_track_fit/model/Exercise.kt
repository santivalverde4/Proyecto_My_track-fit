package com.example.my_track_fit.model

class Exercise (
    //Attributes / constructor
    val id: Int = 0, // Data base assigns automatically the id
    private var name: String,
    private var workout: Workout
) {
    //Getters & setters
    //---name
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String {
        return name
    }
    //---workOut
    fun setWorkout(workOut: Workout){
        this.workout = workOut
    }
    fun getWorkout(): Workout {
        return workout
    }
}