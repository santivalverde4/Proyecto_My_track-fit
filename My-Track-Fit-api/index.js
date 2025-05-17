//eliminar

const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Ruta de prueba
app.get('/', (req, res) => {
  res.send('API My-Track-Fit funcionando');
});

// Ruta para login
app.post('/api/login', (req, res) => {
  const { Username, Password } = req.body;
  // Aquí deberías validar con tu base de datos
  if (Username === 'test' && Password === '1234') {
    res.json({ success: true, message: 'Login exitoso', Id: 1 });
  } else {
    res.json({ success: false, message: 'Credenciales incorrectas' });
  }
});

// Ruta para registro
app.post('/api/signup', (req, res) => {
  const { Username, Password } = req.body;
  // Aquí deberías guardar el usuario en tu base de datos
  res.json({ success: true, message: 'Usuario registrado', Id: 2 });
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`API corriendo en http://localhost:${PORT}`);
});