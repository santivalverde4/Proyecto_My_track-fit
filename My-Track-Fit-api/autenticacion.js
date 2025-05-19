import pkg from "mssql";
const { TYPES } = pkg;

class AuthService {
  constructor(execute) {
    this.execute = execute;
  }

  async loginUser(credentials) {
    const { Username: username, Password: password } = credentials;
    const params = {
      inNombreUsuario: [username, TYPES.VarChar],
      inContraseña: [password, TYPES.VarChar],
    };
    try {
      const response = await this.execute("sp_login", params, { outResultCode: TYPES.Int });
      if (response.output && response.output.outResultCode === 0) {
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
    const { Username: username, Password: password, Email: email } = credentials;
    const params = {
      inNombreUsuario: [username, TYPES.VarChar],
      inCorreo: [email, TYPES.VarChar],
      inContraseña: [password, TYPES.VarChar],
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

  async forgotPassword({ Email }) {
    const params = {
      inCorreo: [Email, TYPES.VarChar]
    };
    try {
      const response = await this.execute("sp_VerificarCorreo", params, { OutResultCode: TYPES.Int });
      if (response.output && response.output.OutResultCode === 0) {
        // Aquí deberías enviar el correo con la contraseña encontrada (usa nodemailer o similar)
        // Ejemplo: await sendMail(Email, response.recordset[0].Contraseña);
        return { success: true, message: "Correo enviado correctamente." };
      } else {
        return { success: false, message: "Correo no encontrado." };
      }
    } catch (error) {
      return { success: false, message: "Error al procesar la solicitud." };
    }
  }

  async updateProfile({ Id, Username, Email, Password }) {
    const params = {
      inIdUsuario: [Id, TYPES.Int],
      inNombreUsuario: [Username, TYPES.VarChar],
      inCorreo: [Email, TYPES.VarChar],
      inContraseña: [Password, TYPES.VarChar],
    };
    try {
      const response = await this.execute("sp_actualizar_usuario", params, { outResultCode: TYPES.Int });
      if (response.output && response.output.outResultCode === 0) {
        return { success: true, message: "Perfil actualizado correctamente." };
      } else {
        return { success: false, message: "No se pudo actualizar el perfil." };
      }
    } catch (error) {
      console.error("Error details:", error);
      return { success: false, message: "Error al actualizar el perfil." };
    }
  }

}

export default AuthService;