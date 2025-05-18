package com.example.my_track_fit.model

class Week(
    //Attributes / constructor
    //val id: Int = 0, // Data base assigns automatically the id
    private var blockList: MutableList<Block>,
    private var routine: Routine
) {
    // Getters & setters
    //---blockList
    fun setBlockList(blockList: MutableList<Block>){
        this.blockList = blockList
    }
    fun getBlockList(): MutableList<Block> {
        return blockList
    }


    //---routine
    fun getRoutine(): Routine {
        return routine
    }

    fun setRoutine(routine: Routine) {
        this.routine = routine
    }

    //methods
    fun addBlock(blockName: String) {
        val newBlock = Block(
            name = blockName,
            exerciseInstanceList = mutableListOf(),
            week = this
        )
        blockList.add(newBlock)
    }

    fun deleteBlock(block: Block){
        blockList.remove(block)
    }
}