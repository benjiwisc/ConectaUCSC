package com.ucsc.conectaucsc.data.model

data class Schedule (
    val id: Int=0,
    val record_id: Int,
    val dia: String,
    val hora_inicio: String,
    val hora_fin: String,
    val sala: String?,
    val tipo_clase: String
)