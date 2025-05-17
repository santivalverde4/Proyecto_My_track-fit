import express from "express";
import dotenv from "dotenv";
import { DataSource } from "typeorm";
import AuthService from "./autenticacion.js";
import createAuthRouter from "./routes/auth.js";

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

const AppDataSource = new DataSource({
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

async function execute(storedProcedure, inParams = {}, outParams = {}) {
  try {
    const queryRunner = AppDataSource.createQueryRunner();
    await queryRunner.connect();

    const inputParams = Object.keys(inParams)
      .map((key) => `@${key} = '${inParams[key]}'`)
      .join(", ");
    const outputParams = Object.keys(outParams)
      .map((key) => `@${key} OUTPUT`)
      .join(", ");
    const query = `EXEC ${storedProcedure} ${inputParams} ${
      outputParams ? ", " + outputParams : ""
    }`;

    const result = await queryRunner.query(query);

    await queryRunner.release();
    return result;
  } catch (error) {
    console.error("Query failed due to: " + error);
    throw error;
  }
}

const authService = new AuthService(execute);
const authRouter = createAuthRouter(authService);
app.use("/api", authRouter);

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