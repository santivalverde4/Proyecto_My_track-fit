package com.example.my_track_fit.model

import java.time.LocalDate // Importa la clase para manejar fechas

// Clase que representa el historial de marcas de peso corporal del usuario
class BodyWeight(
    //val id: Int = 0, // El id lo asigna automáticamente la base de datos (comentado)
    private var bodyWeightMarks: MutableList<Mark> // Lista de marcas de peso corporal
) {
    // Setters y getters

    //---bodyWeightMarks
    fun setBodyWeightMarks(bodyWeightMarks: MutableList<Mark>){
        this.bodyWeightMarks = bodyWeightMarks // Cambia la lista de marcas de peso corporal
    }

    fun getBodyWeightMarks(): MutableList<Mark> {
        return bodyWeightMarks // Devuelve la lista de marcas de peso corporal
    }

    // Métodos

    // Agrega una nueva marca de peso corporal con la fecha actual
    fun addBodyWeightMark(weight: Double) {
        val newMark = Mark(
            bodyWeightMark = weight, // Peso registrado
            date = LocalDate.now() // Fecha actual
        )

        bodyWeightMarks.add(newMark) // Agrega la nueva marca a la lista
    }

    // Elimina una marca de peso corporal de la lista
    fun deleteBodyWeightMark(mark: Mark) {
        bodyWeightMarks.remove(mark) // Quita la marca de la lista
    }
}