package com.example.my_track_fit.model

import java.time.LocalDate

class Mark(
    //val id: Int = 0, // Data base assigns automatically the id
    private var bodyWeightMark: Int,
    private var date: LocalDate
) {
    //Getters & setters
    fun getBodyWeightMark(): Int {
        return bodyWeightMark
    }

    fun setBodyWeightMark(newBodyWeightMark: Int) {
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