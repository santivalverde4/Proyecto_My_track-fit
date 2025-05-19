const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "Block",
  tableName: "blocks",
  columns: {
    id: { primary: true, type: "int", generated: true },
    name: { type: "varchar" }
  },
  relations: {
    week: {
      type: "many-to-one",
      target: "Week",
      joinColumn: true,
      eager: true
    },
    exerciseInstances: {
      type: "one-to-many",
      target: "ExerciseInstance",
      inverseSide: "block",
      cascade: true
    }
  }
});