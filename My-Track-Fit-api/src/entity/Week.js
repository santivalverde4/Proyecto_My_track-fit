const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "Week",
  tableName: "weeks",
  columns: {
    id: { primary: true, type: "int", generated: true }
    // Puedes agregar más campos si lo necesitas
  },
  relations: {
    routine: {
      type: "many-to-one",
      target: "Routine",
      joinColumn: true,
      eager: true
    },
    blocks: {
      type: "one-to-many",
      target: "Block",
      inverseSide: "week",
      cascade: true
    }
  }
});