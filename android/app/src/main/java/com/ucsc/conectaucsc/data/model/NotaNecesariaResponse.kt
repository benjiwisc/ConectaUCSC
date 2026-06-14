package com.ucsc.conectaucsc.data.model

data class NotaNecesariaResponse(
    val nota_acumulada: Double,
    val porcentaje_acumulado: Int,
    val porcentaje_restante: Int,
    val nota_necesaria: Double?,
    val estado: String
)
