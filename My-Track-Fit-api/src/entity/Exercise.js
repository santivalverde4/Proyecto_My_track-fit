const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "Exercise",
  tableName: "exercises",
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
    }
  }
});