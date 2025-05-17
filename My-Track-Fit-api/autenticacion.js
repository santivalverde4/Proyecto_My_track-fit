const { TYPES } = require("mssql");

class AuthService {
  constructor(execute) {
    this.execute = execute;
  }

  async loginUser(credentials) {
    const { Username: username, Password: password } = credentials;
    const params = {
      inUsername: [username, TYPES.VarChar],
      inPassword: [password, TYPES.VarChar],
    };
    try {
      const response = await this.execute("sp_login", params, {});
      if (response.output && response.output.outResultCode == 0) {
        let data = response.recordset[0];
        return {
          success: true,
          Id: data.Id,
          Username: username,
        };
      } else {
        return { success: false, message: "Login failed" };
      }
    } catch (error) {
      console.error("Error details:", error);
      throw new Error(`An error occurred while logging in: ${error}`);
    }
  }

  async signUpUser(credentials) {
    const { Username: username, Password: password } = credentials;
    const params = {
      inUsername: [username, TYPES.VarChar],
      inPassword: [password, TYPES.VarChar],
    };
    try {
      const response = await this.execute("sp_crear_usuario", params, { outResultCode: TYPES.Int });
      if (response.output && response.output.outResultCode === 0) {
        return { success: true, message: "User created successfully." };
      } else {
        return { success: false, message: "Sign up failed" };
      }
    } catch (error) {
      console.error("Error details:", error);
      throw new Error(`An error occurred while signing up: ${error}`);
    }
  }
}

module.exports = AuthService;