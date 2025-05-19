const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "User",
  tableName: "users",
  columns: {
    id: { primary: true, type: "int", generated: true },
    username: { type: "varchar", unique: true },
    password: { type: "varchar" },
    confirmed: { type: "bit", default: false }
  },
  relations: {
    workouts: {
      type: "one-to-many",
      target: "Workout",
      inverseSide: "user",
      cascade: true
    }
  }
});