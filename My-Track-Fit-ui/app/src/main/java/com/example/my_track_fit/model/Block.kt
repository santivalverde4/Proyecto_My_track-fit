package com.example.my_track_fit.model

class Block (
    //Attributes / constructor
    //val id: Int = 0, // Data base assigns automatically the id
    private var name: String,
    private var weeks: MutableList<Week>,
    private var routine: Routine
){
    //Setters & getters
    //---name
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String {
        return name
    }
    //---weeks
    fun setWeeks(weeks: MutableList<Week>){
        this.weeks = weeks
    }
    fun getWeeks(): MutableList<Week> {
        return weeks
    }
    //---routine
    fun setRoutine(routine: Routine){
        this.routine = routine
    }
    fun getRoutine(): Routine {
        return routine
    }

    //Methods
    //Crea una nueva semana en base a los ejercicios anteriores, pero con datos vacios
    fun addWeek(previousWeek: Week) {
        // Crear el nuevo objeto Week
        val newWeek = Week(
            exerciseInstanceList = mutableListOf(), // Inicializar la lista vacía
            block = this // Asignar el bloque actual
        )

        // Crear una nueva lista de ExerciseInstance con setsData vacío y asignar la nueva semana
        val newExerciseInstanceList = previousWeek.getExerciseInstanceList().map { exerciseInstance ->
            ExerciseInstance(
                week = newWeek, // Asignar directamente la nueva semana
                exercise = exerciseInstance.getExercise(),
                setsData = mutableMapOf() // Inicializar setsData vacío
            )
        }.toMutableList()

        // Asignar la lista de ExerciseInstance al nuevo Week
        newWeek.setExerciseInstanceList(newExerciseInstanceList)

        // Agregar el nuevo Week a la lista de semanas
        weeks.add(newWeek)
    }
    fun deleteWeek(week: Week) {
        weeks.remove(week)
    }
}