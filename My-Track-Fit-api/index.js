import 'dotenv/config.js';
import express from 'express';
import cors from 'cors';
import nodemailer from 'nodemailer';
import { v4 as uuidv4 } from 'uuid';
import { DataSource } from 'typeorm';
import { UserEntity } from './entity/UserEntity.js';
import { BodyweightEntity } from './entity/BodyweightEntity.js';
import BodyweightService from "./pesocorporal.js";
import createBodyweightRouter from "./routes/body.js";
const app = express();
app.use(cors());
app.use(express.json());

const BASE_URL = 'http://192.168.0.9:3000'; // Cambia si tu IP cambia

// Configuración de TypeORM
export const AppDataSource = new DataSource({
  type: 'mssql',
  host: process.env.SQL_SERVER,
  port: parseInt(process.env.SQL_PORT),
  username: process.env.SQL_USER,
  password: process.env.SQL_PASSWORD,
  database: process.env.SQL_DATABASE,
  synchronize: true, // Solo para desarrollo
  entities: [UserEntity, BodyweightEntity],
  options: {
    encrypt: true,
    trustServerCertificate: true
  }
});

// Inicializa la conexión y las rutas
AppDataSource.initialize()
  .then(() => {
    console.log('Conexión a la base de datos establecida');

    // Servicios y rutas
    const bodyweightService = new BodyweightService();
    app.use("/api/bodyweight", createBodyweightRouter(bodyweightService));

    // --- Confirmación de usuario por correo ---
    const pendingUsers = {}; // { token: { username, password } }

    app.post('/api/signup', (req, res) => {
      const { Username, Password } = req.body;
      const token = uuidv4();
      pendingUsers[token] = { Username, Password };

      const transporter = nodemailer.createTransport({
        service: 'gmail',
        auth: {
          user: 'mytrackfit@gmail.com',
          pass: 'vgcf omys twmk qwun'
        }
      });

      const confirmUrl = `${BASE_URL}/api/confirm/${token}`;
      const mailOptions = {
        from: 'mytrackfit@gmail.com',
        to: Username, // Suponiendo que Username es el correo
        subject: 'Confirma tu cuenta',
        text: `Haz clic en el siguiente enlace para confirmar tu cuenta: ${confirmUrl}`
      };

      transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
          return res.json({ success: false, message: 'Error enviando correo' });
        }
        res.json({ success: true, message: 'Correo de confirmación enviado' });
      });
    });

    app.get('/api/confirm/:token', async (req, res) => {
      const { token } = req.params;
      const user = pendingUsers[token];
      if (user) {
        try {
          const userRepository = AppDataSource.getRepository(UserEntity);
          await userRepository.save({
            username: user.Username,
            password: user.Password, // ¡En producción, hashea la contraseña!
            confirmed: true,
          });
          delete pendingUsers[token];
          res.send('¡Cuenta confirmada y creada!');
        } catch (err) {
          res.status(500).send('Error guardando usuario en la base de datos');
        }
      } else {
        res.send('Token inválido o expirado');
      }
    });

    app.listen(3000, () => console.log(`API corriendo en ${BASE_URL}`));
  })
  .catch((error) => console.log(error));