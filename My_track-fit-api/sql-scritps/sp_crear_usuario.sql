USE [DB_mytrackfit]
GO

/****** Object:  StoredProcedure [dbo].[sp_crear_usuario]    Script Date: 5/8/2025 1:03:17 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE OR ALTER PROCEDURE [dbo].[sp_crear_usuario]
(
    @inUsername VARCHAR(32),
    @inPassword VARCHAR(32),
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
    WHERE Nombre_Usuario = @inUsername AND Contraseña = @inPassword;

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
    INSERT INTO dbo.Usuario (Nombre_Usuario, Contraseña)
    VALUES (@inUsername, @inPassword);

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
GO


