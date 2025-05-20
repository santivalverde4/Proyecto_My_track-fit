const express = require('express');
const cors = require('cors');
const nodemailer = require('nodemailer');
const { v4: uuidv4 } = require('uuid');
// TypeORM y entidad User
const { DataSource } = require('typeorm');
const UserEntity = require('./entity/User'); 

require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const BASE_URL = 'http://192.168.0.9:3000'; // Variable para la URL base

// Configuración de TypeORM
const AppDataSource = new DataSource({
  type: 'mssql',
  host: process.env.SQL_SERVER,
  port: parseInt(process.env.SQL_PORT),
  username: process.env.SQL_USER,
  password: process.env.SQL_PASSWORD,
  database: process.env.SQL_DATABASE,
  synchronize: true,
  entities: [UserEntity],
  options: {
    encrypt: true, // true si usa SSL
    trustServerCertificate: true
  }
});

AppDataSource.initialize()
  .then(() => {
    console.log('Conexión a la base de datos establecida');
    app.listen(3000, () => console.log(`API corriendo en ${BASE_URL}`));
  })
  .catch((error) => console.log(error));

const pendingUsers = {}; // { token: { username, password } }

//ENDPOINTS
app.post('/api/login', async (req, res) => {
  const { Username, Password } = req.body;
  try {
    const userRepository = AppDataSource.getRepository('User');
    const user = await userRepository.findOneBy({ username: Username, password: Password });
    if (!user) {
      return res.json({ success: false, message: 'Usuario o contraseña incorrectos' });
    }
    if (!user.confirmed) {
      return res.json({ success: false, message: 'Debes confirmar tu cuenta antes de iniciar sesión' });
    }
    res.json({ success: true, message: 'Inicio de sesión exitoso', Id: user.id });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error en el servidor' });
  }
});

app.post('/api/signup', (req, res) => {
  const { Username, Password } = req.body;
  const token = uuidv4();
  pendingUsers[token] = { Username, Password };

  //transporter de nodemailer
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
      const userRepository = AppDataSource.getRepository('User');
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
