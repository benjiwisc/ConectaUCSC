package com.ucsc.conectaucsc.data.model

data class Grade (
    val id: Int,
    val record_id: Int,
    val evaluacion: String,
    val nota: Double,
    val porcentaje: Int
)