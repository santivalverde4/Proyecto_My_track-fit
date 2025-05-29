package com.example.my_track_fit.model

import java.time.LocalDate // Importa la clase para manejar fechas

// Clase que representa una marca de peso corporal en una fecha específica
class Mark(
    //val id: Int = 0, // El id lo asigna automáticamente la base de datos (comentado)
    private var bodyWeightMark: Double, // Valor del peso corporal registrado
    private var date: LocalDate // Fecha en la que se registró el peso
) {
    // Getters & setters

    // Devuelve el valor del peso corporal registrado
    fun getBodyWeightMark(): Double {
        return bodyWeightMark
    }

    // Cambia el valor del peso corporal registrado
    fun setBodyWeightMark(newBodyWeightMark: Double) {
        bodyWeightMark = newBodyWeightMark
    }

    // Devuelve la fecha en la que se registró el peso
    fun getDate(): LocalDate {
        return date
    }

    // Cambia la fecha de la marca de peso corporal
    fun setDate(newDate: LocalDate) {
        date = newDate
    }

    // Methods

    // Actualiza la fecha de la marca al día actual
    fun setActualDate(){
        date = LocalDate.now()
    }
}