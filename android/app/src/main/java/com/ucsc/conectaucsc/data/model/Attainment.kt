package com.ucsc.conectaucsc.data.model

data class Attainment (
    val id: Int,
    val record_id: Int,
    val tipo: String,
    val meta: Int,
    val progreso: Int,
    val cumplido: Boolean
)