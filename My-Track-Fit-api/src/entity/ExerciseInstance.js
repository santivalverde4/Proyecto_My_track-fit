const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "ExerciseInstance",
  tableName: "exercise_instances",
  columns: {
    id: { primary: true, type: "int", generated: true }
    // Puedes agregar más campos para setsData, etc.
  },
  relations: {
    block: {
      type: "many-to-one",
      target: "Block",
      joinColumn: true,
      eager: true
    },
    exercise: {
      type: "many-to-one",
      target: "Exercise",
      joinColumn: true,
      eager: true
    }
  }
});