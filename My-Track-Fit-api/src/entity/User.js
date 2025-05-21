const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "User",
  tableName: "Usuario",
  columns: {
    Id: {
      primary: true,
      type: "int",
      generated: true,
    },
    NombreUsuario: {
      type: "varchar",
      length: 64,
    },
    Contraseña: {
      type: "varchar",
      length: 64,
    },
    Correo: {
      type: "varchar",
      length: 256,
    },
    confirmed: {
      type: "bit", 
      default: false,
    },
  },
});