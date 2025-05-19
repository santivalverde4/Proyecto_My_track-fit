import { Entity, PrimaryGeneratedColumn, Column } from "typeorm";

@Entity()
export class UserEntity {
  @PrimaryGeneratedColumn()
  id;

  @Column()
  username;

  @Column()
  email;

  @Column()
  password;

  @Column({ default: false })
  confirmed;
}