USE [DB_mytrackfit]
GO

/****** Object:  StoredProcedure [dbo].[sp_logout]    Script Date: 5/8/2025 1:05:11 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE OR ALTER PROCEDURE [dbo].[sp_logout]
(
    @inUserId INT,
    @outResultCode INT OUTPUT
)
AS
BEGIN
  SET NOCOUNT ON;
  BEGIN TRY

	DECLARE @detail VARCHAR(64);

    SET @outResultCode = 0;

	SELECT @detail = 'Sesión finalizada correctamente';

  END TRY
  BEGIN CATCH
    SET @outResultCode = 50008; -- Error en base de datos

	SELECT Descripcion AS detail
	FROM dbo.Error
	WHERE Codigo = @outResultCode;

  END CATCH
  SET NOCOUNT OFF;
END
GO


