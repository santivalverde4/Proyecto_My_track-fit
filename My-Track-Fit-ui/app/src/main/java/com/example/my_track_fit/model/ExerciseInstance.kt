package com.example.my_track_fit.model

class ExerciseInstance(
    //val id: Int = 0, // Data base assigns automatically the id
    private var week: Week,
    private var exercise: Exercise,
    private var setsData: MutableMap<Int, SetData> //Dictionary where the key is the set number
) {
    //Internal class to represent data for each set
    data class SetData(
        var weight: Int,
        var reps: Int,
        var rpe: Int
    )

    //Getters & setters
    //---week
    fun setWeek(week: Week){
        this.week = week
    }
    fun getWeek(): Week {
        return week
    }
    //---exercise
    fun setExercise(exercise: Exercise){
        this.exercise = exercise
    }
    fun getExercise(): Exercise {
        return exercise
    }

    //Methods
    fun addSet(setNumber: Int, setData: SetData) {
        setsData[setNumber] = setData
    }

    fun getSet(setNumber: Int): SetData? {
        return setsData[setNumber]
    }

    fun removeSet(setNumber: Int) {
        setsData.remove(setNumber)
    }

    fun updateSet(
        setNumber: Int,
        weight: Int? = null,
        reps: Int? = null,
        rpe: Int? = null
    ) {
        val setData = setsData[setNumber]
        if (setData != null) {
            weight?.let { setData.weight = it }
            reps?.let { setData.reps = it }
            rpe?.let { setData.rpe = it }
        } 
        else {
            println("Set with number $setNumber does not exist.")
        }
    }
}