package com.example.my_track_fit.model

import java.io.Serializable // Permite que la clase se pueda serializar (guardar/cargar en archivos o transferir entre componentes)

// Clase que representa una semana dentro de una rutina
class Week(
    //Attributes / constructor
    //val id: Int = 0, // Data base assigns automatically the id
    private var blockList: MutableList<Block>, // Lista de bloques de la semana
    @Transient private var routine: Routine // Referencia a la rutina a la que pertenece (no se serializa)
) : Serializable { 
    
    // Getters & setters

    //---blockList
    fun setBlockList(blockList: MutableList<Block>){
        this.blockList = blockList // Cambia la lista de bloques de la semana
    }
    fun getBlockList(): MutableList<Block> {
        return blockList // Devuelve la lista de bloques de la semana
    }

    //---routine
    fun getRoutine(): Routine {
        return routine // Devuelve la referencia a la rutina a la que pertenece la semana
    }

    fun setRoutine(routine: Routine) {
        this.routine = routine // Cambia la referencia a la rutina
    }

    // Métodos

    // Agrega un nuevo bloque a la semana con el nombre dado
    fun addBlock(blockName: String) {
        val newBlock = Block(
            name = blockName, // Nombre del nuevo bloque
            exerciseInstanceList = mutableListOf(), // Lista vacía de instancias de ejercicios
            week = this // Asocia el bloque a esta semana
        )
        blockList.add(newBlock) // Agrega el nuevo bloque a la lista
    }

    // Elimina un bloque de la semana
    fun deleteBlock(block: Block){
        blockList.remove(block) // Quita el bloque de la lista
    }
}