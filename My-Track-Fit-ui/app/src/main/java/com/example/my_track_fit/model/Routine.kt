package com.example.my_track_fit.model

class Routine (
    //Attributes / constructor
    //val id: Int = 0, // Data base assigns automatically the id
    private var name: String,
    private var blockList: MutableList<Block>,
    private var workout: Workout
) {
    //Getters & setters
    //---name
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String {
        return name
    }
    //---workout
    fun setWorkout(workOut: Workout){
        this.workout = workOut
    }
    fun getWorkout(): Workout {
        return workout
    }
    //---blockList
    fun setBlockList(blockList: MutableList<Block>){
        this.blockList = blockList
    }
    fun getBlockList(): MutableList<Block> {
        return blockList
    }

    //methods
    fun addBlock(blockName: String) {
        // Crear un nuevo objeto Block
        val newBlock = Block(
            name = blockName, // Asignar el nombre pasado por parámetro
            weeks = mutableListOf(), // Inicializar la lista de semanas vacía
            routine = this // Asignar la rutina actual
        )
        // Agregar el nuevo bloque a la lista de bloques
        blockList.add(newBlock)
    }
    fun deleteBlock(block: Block) {
        blockList.remove(block)
    }
}