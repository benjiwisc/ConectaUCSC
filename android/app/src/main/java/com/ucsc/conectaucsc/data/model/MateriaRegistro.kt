package com.ucsc.conectaucsc.data.model

data class MateriaRegistro (
    val id: Int,
    val usuario_materia_id: Int,
    val finalizado: Boolean,
    val fecha_fin: String?
)