import pkg from "mssql";
const { TYPES } = pkg;

class BodyweightService {
  constructor(execute) {
    this.execute = execute;
  }

  async getBodyweights(userId) {
    const params = {
      inIdbodyweight: [userId, TYPES.Int]
    };
    try {
      const response = await this.execute("sp_ver_bodyweight", params, { outResultCode: TYPES.Int });
      return response.recordset || [];
    } catch (error) { throw error; }
  }

  async createBodyweight(userId, peso) {
    const params = {
      inIdUser: [userId, TYPES.Int],
      inPeso: [peso, TYPES.Int]
    };
    try {
      const response = await this.execute("sp_crear_bodyweight", params, { outResultCode: TYPES.Int });
      return response;
    } catch (error) { throw error; }
  }

  async updateBodyweight(id, userId, peso) {
    const params = {
      inIdbodyweight: [id, TYPES.Int],
      inIdUser: [userId, TYPES.Int],
      inPeso: [peso, TYPES.Int]
    };
    try {
      const response = await this.execute("sp_actualizar_bodyweight", params, { outResultCode: TYPES.Int });
      return response;
    } catch (error) { throw error; }
  }

  async deleteBodyweight(id, userId) {
    const params = {
      inIdbodyweight: [id, TYPES.Int],
      inIdUser: [userId, TYPES.Int]
    };
    try {
      const response = await this.execute("sp_borrar_bodyweight", params, { outResultCode: TYPES.Int });
      return response;
    } catch (error) { throw error; }
  }
}

export default BodyweightService;