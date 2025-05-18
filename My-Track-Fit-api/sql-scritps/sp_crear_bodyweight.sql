USE [DB_mytrackfit]
GO

/****** Object:  StoredProcedure [dbo].[sp_crear_bodyweight]    Script Date: 5/17/2025 4:06:44 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[sp_crear_bodyweight](
	@inIdUser INT,
	@inPeso INT,
	@outResultCode INT
)
AS
BEGIN
	DECLARE @faltalogica int;
	SET @faltalogica = 0;
--logica
END
GO