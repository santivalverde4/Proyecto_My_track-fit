package com.example.my_track_fit.model
import java.io.Serializable // Permite que la clase se pueda serializar

// Clase que representa una instancia de un ejercicio dentro de un bloque
class ExerciseInstance(
    //val id: Int = 0, // El id lo asigna automáticamente la base de datos (comentado)
    @Transient private var block: Block, // Referencia al bloque al que pertenece (no se serializa)
    private var exercise: Exercise, // Referencia al ejercicio asociado
    private var setsData: MutableMap<Int, SetData> // Diccionario donde la clave es el número de set y el valor los datos del set
) : Serializable {
    // Clase interna para representar los datos de cada set
    data class SetData(
        var weight: Int, // Peso utilizado en el set
        var reps: Int,   // Repeticiones realizadas en el set
        var rpe: Int     // RPE (esfuerzo percibido) del set
    ) : Serializable

    // Getters y setters

    //---setsData
    fun getSetsData(): MutableMap<Int, SetData> {
        return setsData // Devuelve el mapa de sets
    }
    fun setSetsData(setsData: MutableMap<Int, SetData>){
        this.setsData = setsData // Cambia el mapa de sets
    }

    //---block
    fun setBlock(block: Block){
        this.block = block // Cambia la referencia al bloque
    }
    fun getBlock(): Block {
        return block // Devuelve la referencia al bloque
    }

    //---exercise
    fun setExercise(exercise: Exercise){
        this.exercise = exercise // Cambia la referencia al ejercicio
    }
    fun getExercise(): Exercise {
        return exercise // Devuelve la referencia al ejercicio
    }

    // Métodos

    // Agrega o actualiza un set en el mapa usando el número de set como clave
    fun addSet(setNumber: Int, setData: SetData) {
        setsData[setNumber] = setData // Inserta o reemplaza el set en el mapa
    }

    // Obtiene los datos de un set por su número
    fun getSet(setNumber: Int): SetData? {
        return setsData[setNumber] // Devuelve el set correspondiente o null si no existe
    }

    // Elimina un set del mapa por su número
    fun removeSet(setNumber: Int) {
        setsData.remove(setNumber) // Quita el set del mapa
    }

    // Actualiza los datos de un set existente (peso, repeticiones, rpe)
    fun updateSet(
        setNumber: Int,
        weight: Int? = null,
        reps: Int? = null,
        rpe: Int? = null
    ) {
        val setData = setsData[setNumber] // Obtiene el set a actualizar
        if (setData != null) {
            weight?.let { setData.weight = it } // Actualiza el peso si se proporciona
            reps?.let { setData.reps = it }     // Actualiza las repeticiones si se proporciona
            rpe?.let { setData.rpe = it }       // Actualiza el RPE si se proporciona
        } 
        else {
            println("Set with number $setNumber does not exist.") // Mensaje si el set no existe
        }
    }
}