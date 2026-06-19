package com.ucsc.conectaucsc.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule (
    val id: Int=0,
    val record_id: Int,
    val dia: String,
    val hora_inicio: String,
    val hora_fin: String,
    val sala: String?,
    val tipo_clase: String,
    val record: RecordRelation? = null
)

@Serializable
data class RecordRelation(
    val id: Int,
    val usuario_materia: UsuarioMateriaRelation? = null
)

@Serializable
data class UsuarioMateriaRelation(
    val id: Int,
    val user_id: Int,
    val materia_id: Int,
    val materia: MateriaSimple? = null
)