import { EntitySchema } from "typeorm";

export const BodyweightEntity = new EntitySchema({
  name: "BodyweightEntity",
  tableName: "bodyweight_entity",
  columns: {
    id: {
      primary: true,
      type: "int",
      generated: true,
    },
    peso: {
      type: "int",
    },
    fecha: {
      type: "varchar",
    },
    userId: {
      type: "int",
    },
  },
  relations: {
    user: {
      type: "many-to-one",
      target: "UserEntity",
      joinColumn: { name: "userId" },
      onDelete: "CASCADE",
    },
  },
});