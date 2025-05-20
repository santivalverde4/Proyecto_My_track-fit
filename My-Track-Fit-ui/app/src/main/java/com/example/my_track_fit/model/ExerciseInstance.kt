package com.example.my_track_fit.model
import java.io.Serializable

class ExerciseInstance(
    //val id: Int = 0, // Data base assigns automatically the id
    @Transient private var block: Block,
    private var exercise: Exercise,
    private var setsData: MutableMap<Int, SetData> //Dictionary where the key is the set number
) : Serializable {
    //Internal class to represent data for each set
    data class SetData(
        var weight: Int,
        var reps: Int,
        var rpe: Int
    ) : Serializable

    //Getters & setters
    //---setsData
    fun getSetsData(): MutableMap<Int, SetData> {
        return setsData
    }
    fun setSetsData(setsData: MutableMap<Int, SetData>){
        this.setsData = setsData
    }
    //---block
    fun setBlock(block: Block){
        this.block = block
    }
    fun getBlock(): Block {
        return block
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