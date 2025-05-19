import { AppDataSource } from "./index.js";
import { BodyweightEntity } from "./entity/BodyweightEntity.js";

class BodyweightService {
  constructor() {
    this.repo = AppDataSource.getRepository(BodyweightEntity);
  }

  async getBodyweights(userId) {
    return await this.repo.find({ where: { userId } });
  }

  async createBodyweight(userId, peso) {
    const bodyweight = this.repo.create({
      userId,
      peso,
      fecha: new Date().toISOString().split("T")[0], // YYYY-MM-DD
    });
    return await this.repo.save(bodyweight);
  }

  async updateBodyweight(id, userId, peso) {
    const bodyweight = await this.repo.findOneBy({ id, userId });
    if (!bodyweight) throw new Error("No encontrado");
    bodyweight.peso = peso;
    return await this.repo.save(bodyweight);
  }

  async deleteBodyweight(id, userId) {
    const bodyweight = await this.repo.findOneBy({ id, userId });
    if (!bodyweight) throw new Error("No encontrado");
    return await this.repo.remove(bodyweight);
  }
}

export default BodyweightService;