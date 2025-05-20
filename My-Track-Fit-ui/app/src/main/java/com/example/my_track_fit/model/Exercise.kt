package com.example.my_track_fit.model

import java.io.Serializable

class Exercise (
    //Attributes / constructor
    val id: Int = 0, // Data base assigns automatically the id
    private var name: String,
    @Transient private var workout: Workout
) : Serializable { 
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