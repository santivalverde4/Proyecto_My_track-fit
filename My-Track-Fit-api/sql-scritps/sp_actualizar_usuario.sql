USE [DB_mytrackfit]
GO

/****** Object:  StoredProcedure [dbo].[sp_actualizar_bodyweight]    Script Date: 5/17/2025 4:05:07 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[sp_actualizar_usuario](
	@inIdUser INT,
	@inUsername VARCHAR(64),
    @inCorreo NVARCHAR(256),
    @inPassword NVARCHAR(64),
	@outResultCode INT
)
AS
BEGIN
	DECLARE @faltalogica int;
	SET @faltalogica = 0;
--logica
END
GO