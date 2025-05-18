USE [DB_mytrackfit]
GO

/****** Object:  StoredProcedure [dbo].[sp_VerificarCorreo]    Script Date: 5/18/2025 4:15:14 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[sp_VerificarCorreo](
    @inCorreo NVARCHAR(256),
    @OutResultCode INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        IF EXISTS (SELECT 1 FROM Usuario WHERE Correo = @inCorreo)
        BEGIN
            SELECT Id, NombreUsuario, Contraseña, Correo
            FROM Usuario
            WHERE Correo = @inCorreo;

            SET @OutResultCode = 0; -- Éxito
        END
        ELSE
        BEGIN
            SET @OutResultCode = 50001; -- Username no existe
        END
    END TRY
    BEGIN CATCH
        SET @OutResultCode = 50008; -- Error de base de datos
    END CATCH
END;
GO


