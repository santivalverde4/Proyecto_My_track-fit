const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "Routine",
  tableName: "routines",
  columns: {
    id: { primary: true, type: "int", generated: true },
    name: { type: "varchar" }
  },
  relations: {
    workout: {
      type: "many-to-one",
      target: "Workout",
      joinColumn: true,
      eager: true
    },
    weeks: {
      type: "one-to-many",
      target: "Week",
      inverseSide: "routine",
      cascade: true
    }
  }
});