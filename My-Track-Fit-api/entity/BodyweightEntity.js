import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn } from "typeorm";
import { UserEntity } from "./UserEntity.js";

@Entity()
export class BodyweightEntity {
  @PrimaryGeneratedColumn()
  id;

  @Column()
  peso;

  @Column()
  fecha;

  @Column()
  userId;

  @ManyToOne(() => UserEntity)
  @JoinColumn({ name: "userId" })
  user;
}