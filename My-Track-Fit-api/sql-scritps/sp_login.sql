USE [DB_mytrackfit]
GO

/****** Object:  StoredProcedure [dbo].[sp_login]    Script Date: 5/8/2025 1:04:17 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE OR ALTER PROCEDURE [dbo].[sp_login]
(
    @inUsername VARCHAR(64),
    @inPassword VARCHAR(64),
    @outResultCode INT OUTPUT
)
AS
BEGIN
  SET NOCOUNT ON;
  BEGIN TRY

    DECLARE @userId INT;

    -- Verificar si el usuario existe
    SELECT @userId = Id
    FROM dbo.Usuario
    WHERE Nombre_Usuario = @inUsername;

    IF (@userId IS NULL)
    BEGIN
        SET @outResultCode = 50001; -- Usuario inválido-
		SELECT Descripcion AS detail
		FROM dbo.Error
		WHERE Codigo = @outResultCode;
        RETURN;
    END

    -- Validar contraseña
    DECLARE @passwordCorrecta VARCHAR(32);

    SELECT @passwordCorrecta = Contraseña
    FROM dbo.Usuario
    WHERE Id = @userId;

    IF (@passwordCorrecta <> @inPassword) -- contraseña distinta
    BEGIN

        SET @outResultCode = 50002; -- Contraseña incorrecta

		SELECT Descripcion AS detail
		FROM dbo.Error
		WHERE Codigo = @outResultCode;

        RETURN;
    END

    -- Login exitoso
    

    SET @outResultCode = 0; -- Login exitoso
	SELECT @userId AS Id;

  END TRY
  BEGIN CATCH

    SET @outResultCode = 50008; -- Error general de base de datos

	SELECT Descripcion AS detail
	FROM dbo.Error
	WHERE Codigo = @outResultCode;

  END CATCH
  SET NOCOUNT OFF;
END
GO


