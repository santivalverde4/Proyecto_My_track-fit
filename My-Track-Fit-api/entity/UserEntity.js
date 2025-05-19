import { EntitySchema } from "typeorm";

export const UserEntity = new EntitySchema({
  name: "UserEntity",
  tableName: "user_entity",
  columns: {
    id: {
      primary: true,
      type: "int",
      generated: true,
    },
    username: {
      type: "varchar",
      unique: true,
    },
    email: {
      type: "varchar",
      unique: true,
    },
    password: {
      type: "varchar",
    },
    confirmed: {
      type: "bit",
      default: false,
    },
  },
});