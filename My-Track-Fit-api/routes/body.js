import express from "express";
const router = express.Router();

const createBodyweightRouter = (bodyweightService) => {
  router.get("/:userId", async (req, res) => {
    try {
      const userId = parseInt(req.params.userId);
      const result = await bodyweightService.getBodyweights(userId);
      res.json(result);
    } catch (error) {
      res.status(500).json({ success: false, message: "Error al obtener bodyweights" });
    }
  });

  router.post("/", async (req, res) => {
    try {
      const { userId, peso } = req.body;
      const result = await bodyweightService.createBodyweight(userId, peso);
      res.status(201).json(result);
    } catch (error) {
      res.status(500).json({ success: false, message: "Error al crear bodyweight" });
    }
  });

  router.put("/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const { userId, peso } = req.body;
      const result = await bodyweightService.updateBodyweight(id, userId, peso);
      res.json(result);
    } catch (error) {
      res.status(500).json({ success: false, message: "Error al actualizar bodyweight" });
    }
  });

  router.delete("/:id", async (req, res) => {
    try {
      const id = parseInt(req.params.id);
      const { userId } = req.body;
      await bodyweightService.deleteBodyweight(id, userId);
      res.json({ success: true });
    } catch (error) {
      res.status(500).json({ success: false, message: "Error al borrar bodyweight" });
    }
  });

  return router;
};

export default createBodyweightRouter;