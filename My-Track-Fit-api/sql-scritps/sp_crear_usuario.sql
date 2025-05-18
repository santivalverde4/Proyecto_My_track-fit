USE [DB_mytrackfit]
GO
/****** Object:  StoredProcedure [dbo].[sp_crear_usuario]    Script Date: 5/18/2025 8:28:51 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[sp_crear_usuario]
(
    @inNombreUsuario VARCHAR(64),
	@inCorreo VARCHAR(64),
    @inContraseña VARCHAR(64),
    @outResultCode INT OUTPUT
)
AS
BEGIN
  SET NOCOUNT ON;

  BEGIN TRY
    DECLARE @userId INT;

    -- Verificar si ya existe un usuario con ese nombre de usuario y contraseña
    SELECT @userId = Id
    FROM dbo.Usuario
    WHERE Correo = @inCorreo AND Contraseña = @inContraseña;

    IF (@userId IS NOT NULL)
    BEGIN
        SET @outResultCode = 50008; -- Usuario ya existe con ese username y contraseña
        SELECT Descripcion AS detail
        FROM dbo.Error
        WHERE Codigo = @outResultCode;
        RETURN;
    END
	BEGIN TRANSACTION;
    -- Insertar nuevo usuario
    INSERT INTO dbo.Usuario (NombreUsuario, Correo, Contraseña)
    VALUES (@inNombreUsuario, @inCorreo, @inContraseña);

    SET @outResultCode = 0; -- Usuario creado exitosamente
	COMMIT;

  END TRY
  BEGIN CATCH
	IF @@TRANCOUNT > 0
		ROLLBACK;

    SET @outResultCode = 50008; -- Error general de base de datos

    SELECT Descripcion AS detail
    FROM dbo.Error
    WHERE Codigo = @outResultCode;
  END CATCH

  SET NOCOUNT OFF;
END
