require('dotenv').config();
const express = require('express');
const cors = require('cors');
const nodemailer = require('nodemailer');
const { v4: uuidv4 } = require('uuid');
// TypeORM y entidad User
const { DataSource } = require('typeorm');
const UserEntity = require('./entity/User');
const ArchivoUsuarioEntity = require('./entity/ArchivoUsuario'); // <-- IMPORTANTE

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
  entities: [UserEntity, ArchivoUsuarioEntity], // <-- AGREGAR LA ENTIDAD
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
const passwordResetTokens = {}; // { token: email }

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
    to: Username,
    subject: 'Confirma tu cuenta',
    text: `Haz clic en el siguiente enlace para confirmar tu cuenta: ${confirmUrl}`,
    html: `
      <div style="font-family: Arial, sans-serif; color: #222;">
        <h2>¡Bienvenido a MyTrackFit!</h2>
        <p>Gracias por registrarte. Por favor, confirma tu cuenta haciendo clic en el siguiente botón:</p>
        <a href="${confirmUrl}" style="display:inline-block;padding:12px 24px;background:#1976d2;color:#fff;text-decoration:none;border-radius:4px;margin:16px 0;">Confirmar cuenta</a>
        <p>O copia y pega este enlace en tu navegador:<br>
        <span style="color:#1976d2">${confirmUrl}</span></p>
        <hr>
        <small>Si no solicitaste esta cuenta, puedes ignorar este correo.</small>
      </div>
    `
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
        password: user.Password, // ¡En producción, hashear la contraseña!
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

//enviar correo de cambio de contraseña
app.post('/api/request-password-reset', async (req, res) => {
  const { email } = req.body;
  const token = uuidv4();
  passwordResetTokens[token] = email;

  // Configura tu transporter de nodemailer
  const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: 'mytrackfit@gmail.com',
      pass: 'vgcf omys twmk qwun'
    }
  });

  const resetUrl = `${BASE_URL}/reset-password/${token}`;
  const mailOptions = {
    from: 'mytrackfit@gmail.com',
    to: email,
    subject: 'Restablece tu contraseña',
    text: `Haz clic en el siguiente enlace para cambiar tu contraseña: ${resetUrl}`,
    html: `
      <div style="font-family: Arial, sans-serif; color: #222;">
        <h2>Restablecimiento de contraseña</h2>
        <p>Recibimos una solicitud para restablecer tu contraseña. Haz clic en el botón para continuar:</p>
        <a href="${resetUrl}" style="display:inline-block;padding:12px 24px;background:#1976d2;color:#fff;text-decoration:none;border-radius:4px;margin:16px 0;">Cambiar contraseña</a>
        <p>O copia y pega este enlace en tu navegador:<br>
        <span style="color:#1976d2">${resetUrl}</span></p>
        <hr>
        <small>Si no solicitaste este cambio, puedes ignorar este correo.</small>
      </div>
    `
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      return res.json({ success: false, message: 'Error enviando correo' });
    }
    res.json({ success: true, message: 'Correo de cambio de contraseña enviado' });
  });
});

//pagina de cambio de contraseña
app.get('/reset-password/:token', (req, res) => {
  const { token } = req.params;
  if (!passwordResetTokens[token]) {
    return res.send('Enlace inválido o expirado');
  }
  // Página simple de cambio de contraseña
  res.send(`
    <html>
      <head>
        <title>Cambiar contraseña</title>
        <style>
          body { font-family: Arial, sans-serif; background: #f7f7f7; }
          form { 
            background: #fff; 
            padding: 24px; 
            border-radius: 8px; 
            box-shadow: 0 2px 8px rgba(0,0,0,0.1); 
            max-width: 320px; 
            margin: 60px auto;
            display: flex;
            flex-direction: column;
            gap: 12px;
          }
          label { font-weight: bold; }
          input[type="password"] { padding: 8px; border-radius: 4px; border: 1px solid #ccc; }
          button { background: #1976d2; color: #fff; border: none; padding: 10px; border-radius: 4px; cursor: pointer; }
          button:hover { background: #1565c0; }
          .error { color: #d32f2f; font-size: 0.95em; }
        </style>
        <script>
          function validateForm() {
            var pass1 = document.getElementById('password').value;
            var pass2 = document.getElementById('password2').value;
            var error = document.getElementById('error-msg');
            // Regex: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial
            var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$/;
            if (pass1 !== pass2) {
              error.textContent = 'Las contraseñas no coinciden';
              return false;
            }
            if (!passwordRegex.test(pass1)) {
              error.textContent = 'La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.';
              return false;
            }
            error.textContent = '';
            return true;
          }
        </script>
      </head>
      <body>
        <form method="POST" action="/api/reset-password/${token}" onsubmit="return validateForm();">
          <label>Nueva contraseña:</label>
          <input type="password" id="password" name="password" required />
          <label>Repite la nueva contraseña:</label>
          <input type="password" id="password2" name="password2" required />
          <span id="error-msg" class="error"></span>
          <button type="submit">Cambiar contraseña</button>
        </form>
      </body>
    </html>
  `);
});

//cambio de contraseña
app.post('/api/reset-password/:token', express.urlencoded({ extended: true }), async (req, res) => {
  const { token } = req.params;
  const { password } = req.body;
  const email = passwordResetTokens[token];
  if (!email) {
    return res.send('Enlace inválido o expirado');
  }
  try {
    const userRepository = AppDataSource.getRepository('User');
    const user = await userRepository.findOneBy({ username: email });
    if (!user) {
      return res.send('Usuario no encontrado');
    }
    user.password = password; // ¡En producción, hashear la contraseña!
    await userRepository.save(user);
    delete passwordResetTokens[token];
    res.send('Contraseña cambiada exitosamente');
  } catch (err) {
    res.status(500).send('Error actualizando la contraseña');
  }
});

// ----------- ENDPOINTS PARA ARCHIVOS DE USUARIO -----------

// Subir archivos del usuario
app.post('/api/upload-user-files', async (req, res) => {
  const { email, ArchivoBody, ArchivoRutina, ArchivoEjercicio } = req.body;
  try {
    const userRepository = AppDataSource.getRepository('User');
    const archivoRepository = AppDataSource.getRepository('ArchivosUsuario');
    const user = await userRepository.findOneBy({ username: email });
    if (!user) return res.status(404).json({ success: false, message: 'Usuario no encontrado' });

    // Busca si ya existe un registro de archivos para este usuario
    let archivos = await archivoRepository.findOne({ where: { Usuario: { id: user.id } } });
    if (!archivos) {
      archivos = archivoRepository.create({
        ArchivoBody,
        ArchivoRutina,
        ArchivoEjercicio,
        Usuario: user
      });
    } else {
      archivos.ArchivoBody = ArchivoBody;
      archivos.ArchivoRutina = ArchivoRutina;
      archivos.ArchivoEjercicio = ArchivoEjercicio;
    }
    await archivoRepository.save(archivos);
    res.json({ success: true, message: 'Archivos guardados' });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error en el servidor' });
  }
});

// Descargar archivos del usuario
app.get('/api/download-user-files', async (req, res) => {
  const { email } = req.query;
  try {
    const userRepository = AppDataSource.getRepository('User');
    const archivoRepository = AppDataSource.getRepository('ArchivosUsuario');
    const user = await userRepository.findOneBy({ username: email });
    if (!user) return res.status(404).json({ success: false, message: 'Usuario no encontrado' });

    const archivos = await archivoRepository.findOne({ where: { Usuario: { id: user.id } } });
    if (!archivos) {
      return res.json({ ArchivoBody: "", ArchivoRutina: "", ArchivoEjercicio: "" });
    }
    res.json({
      ArchivoBody: archivos.ArchivoBody || "",
      ArchivoRutina: archivos.ArchivoRutina || "",
      ArchivoEjercicio: archivos.ArchivoEjercicio || ""
    });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error en el servidor' });
  }
});

app.listen(3000, () => console.log(`API corriendo en ${BASE_URL}`));