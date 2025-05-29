# My Track-Fit 🏋️‍♂️

Una aplicación completa de seguimiento de ejercicios y fitness que permite a los usuarios monitorear sus rutinas, progreso y objetivos de entrenamiento.

## 📱 Características Principales

- ✅ Registro y seguimiento de ejercicios
- 📊 Análisis de progreso y estadísticas
- 🎯 Establecimiento de objetivos personalizados
- 📈 Visualización de datos de entrenamiento
- 🔐 Sistema de autenticación seguro
- 📱 Interfaz móvil intuitiva

## 🛠️ Tecnologías Utilizadas

### Backend
<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nodejs/nodejs-original.svg" alt="Node.js" width="60" height="60"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/express/express-original.svg" alt="Express.js" width="60" height="60"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/javascript/javascript-original.svg" alt="JavaScript" width="60" height="60"/>
  <img src="https://raw.githubusercontent.com/typeorm/typeorm/master/resources/logo_big.png" alt="TypeORM" width="60" height="60"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/microsoftsqlserver/microsoftsqlserver-plain.svg" alt="SQL Server" width="60" height="60"/>
</div>

- **Node.js**: Entorno de ejecución para JavaScript en el servidor
- **Express.js**: Framework web minimalista para Node.js
- **JavaScript**: Lenguaje de programación principal
- **TypeORM**: ORM (Object-Relational Mapping) para TypeScript y JavaScript
- **SQL Server**: Sistema de gestión de base de datos relacional

### Mobile
<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" alt="Kotlin" width="60" height="60"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg" alt="Android" width="60" height="60"/>
</div>

- **Kotlin**: Lenguaje de programación moderno para desarrollo Android
- **Android**: Plataforma móvil objetivo

## 📋 Requisitos Previos

Antes de ejecutar este proyecto, asegúrate de tener instalado:

- [Node.js](https://nodejs.org/) (versión 14.x o superior)
- [npm](https://www.npmjs.com/) o [yarn](https://yarnpkg.com/)
- [SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
- [Android Studio](https://developer.android.com/studio) (para desarrollo móvil)
- [JDK 8+](https://adoptopenjdk.net/) (para Kotlin/Android)

## ⚙️ Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/santivalverde4/my-track-fit.git
cd my-track-fit
```

### 2. Configuración del Backend

#### Instalar Dependencias
```bash
cd backend
npm install
```

#### Configurar Variables de Entorno
Crea un archivo `.env` en la carpeta `backend`:

```env
# Configuración del Servidor
PORT=3000
NODE_ENV=development

# Configuración de Base de Datos
DB_HOST=localhost
DB_PORT=1433
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
DB_DATABASE=my_track_fit

# JWT Configuration
JWT_SECRET=tu_jwt_secret_key
JWT_EXPIRES_IN=24h

# Configuración CORS
CORS_ORIGIN=http://localhost:3000
```

#### Configurar Base de Datos
```bash
# Ejecutar migraciones
npm run migration:run

# Poblar base de datos (opcional)
npm run seed
```

### 3. Configuración de la App Android

#### Abrir en Android Studio
```bash
cd android-app
# Abrir el proyecto en Android Studio
```

#### Configurar Conexión API
En `app/src/main/java/config/ApiConstants.kt`:

```kotlin
object ApiConstants {
    const val BASE_URL = "http://tu-servidor:3000/api/"
    const val TIMEOUT_SECONDS = 30L
}
```

## 🚀 Ejecución

### Ejecutar Backend
```bash
cd backend

# Modo desarrollo
npm run dev

# Modo producción
npm start
```

### Ejecutar App Android
1. Abrir Android Studio
2. Sincronizar proyecto con Gradle
3. Ejecutar en emulador o dispositivo físico

## 📁 Estructura del Proyecto

```
my-track-fit/
├── backend/
│   ├── src/
│   │   ├── controllers/     # Controladores de API
│   │   ├── entities/        # Entidades de TypeORM
│   │   ├── middlewares/     # Middlewares personalizados
│   │   ├── routes/          # Definición de rutas
│   │   ├── services/        # Lógica de negocio
│   │   └── utils/           # Utilidades
│   ├── migrations/          # Migraciones de BD
│   ├── package.json
│   └── server.js
└── android-app/
    ├── app/
    │   ├── src/main/java/   # Código Kotlin
    │   ├── src/main/res/    # Recursos Android
    │   └── build.gradle
    └── gradle/
```

## 🔧 API Endpoints

### Autenticación
```http
POST /api/auth/register    # Registrar usuario
POST /api/auth/login       # Iniciar sesión
POST /api/auth/logout      # Cerrar sesión
```

### Usuarios
```http
GET    /api/users/profile  # Obtener perfil
PUT    /api/users/profile  # Actualizar perfil
DELETE /api/users/account  # Eliminar cuenta
```

### Ejercicios
```http
GET    /api/exercises      # Listar ejercicios
POST   /api/exercises      # Crear ejercicio
PUT    /api/exercises/:id  # Actualizar ejercicio
DELETE /api/exercises/:id  # Eliminar ejercicio
```

### Rutinas
```http
GET    /api/routines       # Listar rutinas
POST   /api/routines       # Crear rutina
PUT    /api/routines/:id   # Actualizar rutina
DELETE /api/routines/:id   # Eliminar rutina
```

## 🗃️ Base de Datos

### Esquema Principal

```sql
-- Tabla de Usuarios
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

-- Tabla de Ejercicios
CREATE TABLE exercises (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES users(id),
    name NVARCHAR(100) NOT NULL,
    description NTEXT,
    muscle_group NVARCHAR(50),
    created_at DATETIME DEFAULT GETDATE()
);

-- Tabla de Rutinas
CREATE TABLE routines (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES users(id),
    name NVARCHAR(100) NOT NULL,
    description NTEXT,
    created_at DATETIME DEFAULT GETDATE()
);
```

## 🧪 Pruebas

### Backend
```bash
# Ejecutar pruebas unitarias
npm test

# Ejecutar pruebas con cobertura
npm run test:coverage
```

### Android
```bash
# Ejecutar pruebas unitarias
./gradlew test

# Ejecutar pruebas instrumentadas
./gradlew connectedAndroidTest
```

## 📦 Despliegue

### Backend (Producción)
```bash
# Construir aplicación
npm run build

# Desplegar con PM2
pm2 start ecosystem.config.js --env production
```

### Android
```bash
# Generar APK de release
./gradlew assembleRelease

# Generar AAB para Play Store
./gradlew bundleRelease
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ve el archivo [LICENSE.md](LICENSE.md) para detalles.

## ✨ Desarrolladores

### **Santiago Valverde**
- GitHub: [@santivalverde4](https://github.com/santivalverde4)

### **Dilan Hernández**
- GitHub: [@DilanHern](https://github.com/DilanHern)

## 🙏 Agradecimientos

- Dr. Ing. Prof. Mario Chacón Rivas (curso de Requerimientos de Software).
- Recursos de iconos de [DevIcons](https://devicon.dev/)

---

⭐ ¡No olvides dar una estrella al proyecto si te ha sido útil!
