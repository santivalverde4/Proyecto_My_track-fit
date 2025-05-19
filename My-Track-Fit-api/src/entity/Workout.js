const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "Workout",
  tableName: "workouts",
  columns: {
    id: { primary: true, type: "int", generated: true }
  },
  relations: {
    user: {
      type: "many-to-one",
      target: "User",
      joinColumn: true,
      eager: true
    },
    routines: {
      type: "one-to-many",
      target: "Routine",
      inverseSide: "workout",
      cascade: true
    },
    exercises: {
      type: "one-to-many",
      target: "Exercise",
      inverseSide: "workout",
      cascade: true
    }
  }
});