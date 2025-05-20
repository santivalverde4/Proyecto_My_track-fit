package com.example.my_track_fit.model

import java.time.LocalDate

class Mark(
    //val id: Int = 0, // Data base assigns automatically the id
    private var bodyWeightMark: Double,
    private var date: LocalDate
) {
    //Getters & setters
    fun getBodyWeightMark(): Double {
        return bodyWeightMark
    }

    fun setBodyWeightMark(newBodyWeightMark: Double) {
        bodyWeightMark = newBodyWeightMark
    }

    fun getDate(): LocalDate {
        return date
    }

    fun setDate(newDate: LocalDate) {
        date = newDate
    }

    //Methods
    fun setActualDate(){
        date = LocalDate.now()
    }
}