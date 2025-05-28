require('dotenv').config(); // Carga variables de entorno desde .env
const express = require('express'); // Framework para crear el servidor HTTP
const cors = require('cors'); // Middleware para permitir peticiones de otros orígenes
const nodemailer = require('nodemailer'); // Librería para enviar correos electrónicos
const { v4: uuidv4 } = require('uuid'); // Para generar tokens únicos (UUID)
// TypeORM y entidad User
const { DataSource } = require('typeorm'); // ORM para manejar la base de datos
const UserEntity = require('./entity/User'); // Entidad de usuario
const ArchivoUsuarioEntity = require('./entity/ArchivoUsuario'); // Entidad para archivos de usuario

const app = express(); // Crea la aplicación de Express
app.use(cors()); // Habilita CORS para todas las rutas
app.use(express.json()); // Permite recibir JSON en las peticiones

const BASE_URL = 'http://10.0.2.2:3000'; // Variable para la URL base

// Configuración de TypeORM para conectarse a SQL Server
const AppDataSource = new DataSource({
  type: 'mssql', // Tipo de base de datos
  host: process.env.SQL_SERVER, // Host de la base de datos
  port: parseInt(process.env.SQL_PORT), // Puerto
  username: process.env.SQL_USER, // Usuario
  password: process.env.SQL_PASSWORD, // Contraseña
  database: process.env.SQL_DATABASE, // Nombre de la base de datos
  synchronize: true, // Sincroniza entidades automáticamente (solo para desarrollo)
  entities: [UserEntity, ArchivoUsuarioEntity], // Entidades a usar
  options: {
    encrypt: true, // true si usa SSL
    trustServerCertificate: true // Confía en el certificado del servidor
  }
});

// Inicializa la conexión a la base de datos
AppDataSource.initialize()
  .then(() => {
    console.log('Conexión a la base de datos establecida');
    app.listen(3000, () => console.log(`API corriendo en ${BASE_URL}`)); // Inicia el servidor en el puerto 3000
  })
  .catch((error) => console.log(error)); // Muestra errores de conexión

const pendingUsers = {}; // { token: { username, password } } // Usuarios pendientes de confirmar
const passwordResetTokens = {}; // { token: email } // Tokens para recuperación de contraseña

// ENDPOINTS

// Endpoint para login de usuario
app.post('/api/login', async (req, res) => {
  const { Username, Password } = req.body; // Obtiene usuario y contraseña del body
  try {
    const userRepository = AppDataSource.getRepository('User'); // Repositorio de usuarios
    const user = await userRepository.findOneBy({ username: Username, password: Password }); // Busca usuario por usuario y contraseña
    if (!user) {
      return res.json({ success: false, message: 'Usuario o contraseña incorrectos' }); // Usuario no encontrado
    }
    if (!user.confirmed) {
      return res.json({ success: false, message: 'Debes confirmar tu cuenta antes de iniciar sesión' }); // Usuario no confirmado
    }
    res.json({ success: true, message: 'Inicio de sesión exitoso', Id: user.id }); // Login exitoso
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error en el servidor' }); // Error de servidor
  }
});

// Endpoint para registro de usuario
app.post('/api/signup', async (req, res) => {
  const { Username, Password } = req.body; // Obtiene usuario y contraseña del body
  try {
    const userRepository = AppDataSource.getRepository('User'); // Repositorio de usuarios
    // Verifica si el usuario ya existe
    const existingUser = await userRepository.findOneBy({ username: Username });
    if (existingUser) {
      return res.json({ success: false, message: 'El correo ya está registrado.' }); // Usuario ya existe
    }

    const token = uuidv4(); // Genera un token único
    pendingUsers[token] = { Username, Password }; // Guarda usuario pendiente de confirmación

    // Configura el transporter de nodemailer para enviar correos
    const transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        user: 'mytrackfit@gmail.com',
        pass: 'vgcf omys twmk qwun'
      }
    });

    const confirmUrl = `${BASE_URL}/api/confirm/${token}`; // URL de confirmación
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

    // Envía el correo de confirmación
    transporter.sendMail(mailOptions, (error, info) => {
      if (error) {
        return res.json({ success: false, message: 'Error enviando correo' });
      }
      res.json({ success: true, message: 'Correo de confirmación enviado' });
    });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Error en el servidor' });
  }
});

// Endpoint para confirmar cuenta con el token recibido por correo
app.get('/api/confirm/:token', async (req, res) => {
  const { token } = req.params; // Obtiene el token de la URL
  const user = pendingUsers[token]; // Busca el usuario pendiente
  if (user) {
    try {
      const userRepository = AppDataSource.getRepository('User');
      await userRepository.save({
        username: user.Username,
        password: user.Password, // ¡En producción, hashear la contraseña!
        confirmed: true,
      });
      delete pendingUsers[token]; // Elimina el usuario pendiente tras confirmar
      // Página de éxito estilizada
      res.send(`
        <html>
          <head>
            <title>Cuenta confirmada</title>
            <style>
              body { background: #f7f7f7; font-family: Arial, sans-serif; }
              .container {
                background: #fff;
                max-width: 400px;
                margin: 80px auto;
                padding: 32px 24px;
                border-radius: 10px;
                box-shadow: 0 2px 12px rgba(0,0,0,0.12);
                text-align: center;
              }
              .icon {
                font-size: 48px;
                color: #43a047;
                margin-bottom: 16px;
              }
              h2 { color: #1976d2; margin-bottom: 8px; }
              p { color: #444; }
            </style>
          </head>
          <body>
            <div class="container">
              <div class="icon">✅</div>
              <h2>¡Cuenta confirmada!</h2>
              <p>Tu cuenta ha sido creada exitosamente.<br>Puedes iniciar sesión en la app.</p>
            </div>
          </body>
        </html>
      `);
    } catch (err) {
      // Página de error estilizada
      res.status(500).send(`
        <html>
          <head>
            <title>Error</title>
            <style>
              body { background: #f7f7f7; font-family: Arial, sans-serif; }
              .container {
                background: #fff;
                max-width: 400px;
                margin: 80px auto;
                padding: 32px 24px;
                border-radius: 10px;
                box-shadow: 0 2px 12px rgba(0,0,0,0.12);
                text-align: center;
              }
              .icon { font-size: 48px; color: #d32f2f; margin-bottom: 16px; }
              h2 { color: #d32f2f; margin-bottom: 8px; }
              p { color: #444; }
            </style>
          </head>
          <body>
            <div class="container">
              <div class="icon">❌</div>
              <h2>Error</h2>
              <p>Ocurrió un error guardando el usuario en la base de datos.</p>
            </div>
          </body>
        </html>
      `);
    }
  } else {
    // Página de token inválido estilizada
    res.send(`
      <html>
        <head>
          <title>Token inválido</title>
          <style>
            body { background: #f7f7f7; font-family: Arial, sans-serif; }
            .container {
              background: #fff;
              max-width: 400px;
              margin: 80px auto;
              padding: 32px 24px;
              border-radius: 10px;
              box-shadow: 0 2px 12px rgba(0,0,0,0.12);
              text-align: center;
            }
            .icon { font-size: 48px; color: #d32f2f; margin-bottom: 16px; }
            h2 { color: #d32f2f; margin-bottom: 8px; }
            p { color: #444; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="icon">❌</div>
            <h2>Token inválido o expirado</h2>
            <p>El enlace de confirmación no es válido o ya fue utilizado.</p>
          </div>
        </body>
      </html>
    `);
  }
});

// Endpoint para enviar correo de cambio de contraseña
app.post('/api/request-password-reset', async (req, res) => {
  const { email } = req.body; // Obtiene el correo del body
  const token = uuidv4(); // Genera un token único
  passwordResetTokens[token] = email; // Asocia el token al correo

  // Configura el transporter de nodemailer
  const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: 'mytrackfit@gmail.com',
      pass: 'vgcf omys twmk qwun'
    }
  });

  const resetUrl = `${BASE_URL}/reset-password/${token}`; // URL para restablecer contraseña
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

  // Envía el correo de restablecimiento de contraseña
  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      return res.json({ success: false, message: 'Error enviando correo' });
    }
    res.json({ success: true, message: 'Correo de cambio de contraseña enviado' });
  });
});

// Página para cambiar la contraseña (formulario HTML)
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
            var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&._-])[A-Za-z\\d@\$!%*?&._-]{8,}$/;
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

// Endpoint para cambiar la contraseña (POST desde el formulario)
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
    delete passwordResetTokens[token]; // Elimina el token tras usarlo
    res.send('Contraseña cambiada exitosamente');
  } catch (err) {
    res.status(500).send('Error actualizando la contraseña');
  }
});

// ----------- ENDPOINTS PARA ARCHIVOS DE USUARIO -----------

// Endpoint para subir archivos del usuario (rutinas, ejercicios, peso corporal)
app.post('/api/upload-user-files', async (req, res) => {
  const { email, ArchivoBody, ArchivoRutina, ArchivoEjercicio } = req.body; // Obtiene los archivos y el correo
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
    await archivoRepository.save(archivos); // Guarda los archivos en la base de datos
    res.json({ success: true, message: 'Archivos guardados' });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Error en el servidor' });
  }
});

// Endpoint para descargar archivos del usuario
app.get('/api/download-user-files', async (req, res) => {
  const { email } = req.query; // Obtiene el correo del query string
  try {
    const userRepository = AppDataSource.getRepository('User');
    const archivoRepository = AppDataSource.getRepository('ArchivosUsuario');
    const user = await userRepository.findOneBy({ username: email });
    if (!user) return res.status(404).json({ success: false, message: 'Usuario no encontrado' });

    const archivos = await archivoRepository.findOne({ where: { Usuario: { id: user.id } } });
    if (!archivos) {
      return res.json({ ArchivoBody: "", ArchivoRutina: "", ArchivoEjercicio: "" }); // Si no hay archivos, devuelve vacíos
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

// Inicia el servidor en el puerto 3000 (por si la inicialización de la base de datos no lo hizo)
app.listen(3000, () => console.log(`API corriendo en ${BASE_URL}`));