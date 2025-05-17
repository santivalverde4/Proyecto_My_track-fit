package com.example.my_track_fit.model

import java.time.LocalDate

class BodyWeight(
    //val id: Int = 0, // Data base assigns automatically the id
    private var bodyWeightMarks: MutableList<Mark>
) {
    //Setters & getters
    //---bodyWeightMarks
    fun setBodyWeightMarks(bodyWeightMarks: MutableList<Mark>){
        this.bodyWeightMarks = bodyWeightMarks
    }

    fun getBodyWeightMarks(): MutableList<Mark> {
        return bodyWeightMarks
    }

    //Methods
    fun addBodyWeightMark(weight: Int) {
        val newMark = Mark(
            bodyWeightMark = weight,
            date = LocalDate.now()
        )

        bodyWeightMarks.add(newMark)
    }

    fun deleteBodyWeightMark(mark: Mark) {
        bodyWeightMarks.remove(mark)
    }
}