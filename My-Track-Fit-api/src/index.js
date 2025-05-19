require('dotenv').config();
const express = require('express');
const cors = require('cors');
const nodemailer = require('nodemailer');
const { v4: uuidv4 } = require('uuid');
// TypeORM y entidad User
const { DataSource } = require('typeorm');
const UserEntity = require('./entity/User');
const WorkoutEntity = require('./entity/Workout');
const RoutineEntity = require('./entity/Routine');
const WeekEntity = require('./entity/Week');
const BlockEntity = require('./entity/Block');
const ExerciseEntity = require('./entity/Exercise');
const ExerciseInstanceEntity = require('./entity/ExerciseInstance');



const app = express();
app.use(cors());
app.use(express.json());

const BASE_URL = 'http://192.168.100.153:3000'; // Variable para la URL base

// Configuración de TypeORM
const AppDataSource = new DataSource({
  type: 'mssql',
  host: process.env.SQL_SERVER,
  port: parseInt(process.env.SQL_PORT),
  username: process.env.SQL_USER,
  password: process.env.SQL_PASSWORD,
  database: process.env.SQL_DATABASE,
  synchronize: true,
  entities: [
    UserEntity,
    WorkoutEntity,
    RoutineEntity,
    WeekEntity,
    BlockEntity,
    ExerciseEntity,
    ExerciseInstanceEntity
  ],
  options: {
    encrypt: true,
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
//login
app.post('/api/login', async (req, res) => {
  const { Username, Password } = req.body;
  try {
    const userRepository = AppDataSource.getRepository('User');
    const workoutRepository = AppDataSource.getRepository('Workout');
    const user = await userRepository.findOneBy({ username: Username, password: Password });
    if (!user) {
      return res.json({ success: false, message: 'Usuario o contraseña incorrectos' });
    }
    if (!user.confirmed) {
      return res.json({ success: false, message: 'Debes confirmar tu cuenta antes de iniciar sesión' });
    }
    // Busca el workout vinculado a este usuario
    const workout = await workoutRepository.findOneBy({ user: { id: user.id } });
    res.json({
      success: true,
      message: 'Inicio de sesión exitoso',
      Id: user.id,
      workoutId: workout ? workout.id : null // <-- Devuelve el workoutId
    });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error en el servidor' });
  }
});

//signup
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

//link de confirmación
app.get('/api/confirm/:token', async (req, res) => {
  const { token } = req.params;
  const user = pendingUsers[token];
  if (user) {
    try {
      const userRepository = AppDataSource.getRepository('User');
      const workoutRepository = AppDataSource.getRepository('Workout');
      // 1. Crea el usuario
      const savedUser = await userRepository.save({
        username: user.Username,
        password: user.Password, // ¡En producción, hashea la contraseña!
        confirmed: true,
      });
      // 2. Crea el workout vinculado al usuario
      await workoutRepository.save({
        user: savedUser
      });
      delete pendingUsers[token];
      res.send('¡Cuenta confirmada y workout creado!');
    } catch (err) {
      res.status(500).send('Error guardando usuario o workout en la base de datos');
    }
  } 
  else {
    res.send('Token inválido o expirado');
  }
});

//Añadir ejercicio
app.post('/api/exercise', async (req, res) => {
  const { name, workoutId } = req.body;
  try {
    const workoutRepository = AppDataSource.getRepository('Workout');
    const exerciseRepository = AppDataSource.getRepository('Exercise');
    const workout = await workoutRepository.findOneBy({ id: workoutId });
    if (!workout) {
      return res.status(404).json({ success: false, message: 'Workout no encontrado' });
    }
    const exercise = await exerciseRepository.save({
      name,
      workout
    });
    res.json({ success: true, exercise });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error al crear ejercicio' });
  }
});

//obtener workout
app.get('/api/workout/:id', async (req, res) => {
  const workoutId = parseInt(req.params.id, 10);
  try {
    const workoutRepository = AppDataSource.getRepository('Workout');
    // Incluye relaciones: rutinas y ejercicios
    const workout = await workoutRepository.findOne({
      where: { id: workoutId },
      relations: ['routines', 'exercises']
    });

    if (!workout) {
      return res.status(404).json({ message: 'Workout no encontrado' });
    }

    // Opcional: adapta la estructura si es necesario para el frontend
    res.json({
      id: workout.id,
      routines: workout.routines || [],
      exercises: workout.exercises || []
    });
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener workout' });
  }
});

// Obtener solo los ejercicios de un workout
app.get('/api/workout/:id/exercises', async (req, res) => {
  const workoutId = parseInt(req.params.id, 10);
  try {
    const exerciseRepository = AppDataSource.getRepository('Exercise');
    const exercises = await exerciseRepository.find({
      where: { workout: { id: workoutId } }
    });
    res.json({ exercises });
  } catch (err) {
    res.status(500).json({ message: 'Error al obtener ejercicios' });
  }
});

//Cambiar nombre de ejercicio
app.put('/api/exercise/:id', async (req, res) => {
  const exerciseId = parseInt(req.params.id, 10);
  const { name } = req.body;
  try {
    const exerciseRepository = AppDataSource.getRepository('Exercise');
    const exercise = await exerciseRepository.findOneBy({ id: exerciseId });
    if (!exercise) {
      return res.status(404).json({ success: false, message: 'Ejercicio no encontrado' });
    }
    exercise.name = name;
    await exerciseRepository.save(exercise);
    res.json({ success: true, exercise });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error al actualizar ejercicio' });
  }
});

//eliminar ejercicio
app.delete('/api/exercise/:id', async (req, res) => {
  const exerciseId = parseInt(req.params.id, 10);
  try {
    const exerciseRepository = AppDataSource.getRepository('Exercise');
    const exercise = await exerciseRepository.findOneBy({ id: exerciseId });
    if (!exercise) {
      return res.status(404).json({ success: false, message: 'Ejercicio no encontrado' });
    }
    await exerciseRepository.remove(exercise);
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error al eliminar ejercicio' });
  }
});

app.listen(3000, () => console.log(`API corriendo en ${BASE_URL}`));