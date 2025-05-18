import express from "express";
const router = express.Router();

const createBodyweightRouter = (bodyweightService) => {
  router.get("/:userId", async (req, res) => {
    const userId = parseInt(req.params.userId);
    const result = await bodyweightService.getBodyweights(userId);
    res.json(result);
  });

  router.post("/", async (req, res) => {
    const { userId, peso } = req.body;
    const result = await bodyweightService.createBodyweight(userId, peso);
    res.json(result);
  });

  router.put("/:id", async (req, res) => {
    const id = parseInt(req.params.id);
    const { userId, peso } = req.body;
    const result = await bodyweightService.updateBodyweight(id, userId, peso);
    res.json(result);
  });

  router.delete("/:id", async (req, res) => {
    const id = parseInt(req.params.id);
    const { userId } = req.body;
    const result = await bodyweightService.deleteBodyweight(id, userId);
    res.json(result);
  });

  return router;
};

export default createBodyweightRouter;