package com.ucsc.conectaucsc.data.model

data class Record (
    val id: Int,
    val usuario_materia_id: Int,
    val finalizado: Boolean,
    val fecha_fin: String?
)