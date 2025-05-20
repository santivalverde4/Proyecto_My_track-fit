const { EntitySchema } = require("typeorm");

module.exports = new EntitySchema({
  name: "ArchivosUsuario",
  tableName: "ArchivosUsuario",
  columns: {
    Id: {
      primary: true,
      type: "int",
      generated: true,
    },
    ArchivoBody: {
      type: "nvarchar",
      length: "MAX",
      nullable: true,
    },
    ArchivoRutina: {
      type: "nvarchar",
      length: "MAX",
      nullable: true,
    },
    ArchivoEjercicio: {
      type: "nvarchar",
      length: "MAX",
      nullable: true,
    },
  },
  relations: {
    Usuario: {
      type: "many-to-one",
      target: "User",
      joinColumn: { name: "Idcliente" },
      onDelete: "CASCADE",
      nullable: false,
    },
  },
});