const express = require("express");
const router = express.Router();

module.exports = (authService) => {
  // Endpoint para login
  router.post("/login", async (req, res) => {
    const { Username, Password } = req.body;
    try {
      const result = await authService.loginUser({ Username, Password });
      if (result.success) {
        res.status(200).json(result);
      } else {
        res.status(401).json({ success: false, message: "Credenciales inválidas" });
      }
    } catch (error) {
      res.status(500).json({ success: false, message: "Error interno del servidor" });
    }
  });

  // Endpoint para sign-up
  router.post("/signup", async (req, res) => {
    const { Username, Password } = req.body;
    try {
      const result = await authService.signUpUser({ Username, Password });
      if (result.success) {
        res.status(201).json(result);
      } else {
        res.status(400).json({ success: false, message: result.message });
      }
    } catch (error) {
      res.status(500).json({ success: false, message: "Error interno del servidor" });
    }
  });

  return router;
};