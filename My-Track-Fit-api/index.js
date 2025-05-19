import express from "express";
import dotenv from "dotenv";
import { DataSource } from "typeorm";
import sql from "mssql";
import AuthService from "./autenticacion.js";
import createAuthRouter from "./routes/auth.js";
import createBodyweightRouter from "./routes/bodyweight.js";

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3050;

app.use(express.json());

export const AppDataSource = new DataSource({
  type: "mssql",
  host: process.env.SQL_SERVER,
  port: Number(process.env.SQL_PORT) || 1433,
  username: process.env.SQL_USER,
  password: process.env.SQL_PASSWORD,
  database: process.env.SQL_DATABASE,
  synchronize: false,
  logging: true,
  options: {
    encrypt: true,
    trustServerCertificate: true,
  },
});

async function initConnection() {
  try {
    console.log("Connecting to the database...");
    await AppDataSource.initialize();
    console.log("Database connection successful.");
  } catch (error) {
    console.error("Connection failed due to: " + error);
    throw error;
  }
}

// NUEVA función execute usando mssql para parámetros OUTPUT
async function execute(storedProcedure, inParams = {}, outParams = {}) {
  try {
    // Conexión directa usando mssql y la config de AppDataSource
    const pool = await sql.connect(AppDataSource.options);
    const request = pool.request();

    // Parámetros de entrada
    for (const key in inParams) {
      request.input(key, inParams[key][1], inParams[key][0]);
    }
    // Parámetros de salida
    for (const key in outParams) {
      request.output(key, outParams[key]);
    }

    const result = await request.execute(storedProcedure);
    return result;
  } catch (error) {
    console.error("Query failed due to: " + error);
    throw error;
  }
}

const authService = new AuthService(execute);
const authRouter = createAuthRouter(authService);
app.use("/api", authRouter);

app.use("/api/bodyweight", createBodyweightRouter(execute));

app.get("/", (req, res) => {
  res.send("API is running...");
});

(async () => {
  try {
    await initConnection();
    app.listen(PORT, "0.0.0.0", () => {
      console.log(`Server is running on http://0.0.0.0:${PORT}`);
    });
  } catch (error) {
    console.error("Failed to start the server:", error);
  }
})();