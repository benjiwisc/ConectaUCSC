package com.ucsc.conectaucsc.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SesionEstudio(
    val id: Int,
    val titulo: String,
    val lugar: String,
    val fecha_hora: String,
    val descripcion: String?,
    val materia_id: Int,
    val user_id: Int,
    val creador: UsuarioSimple?,
    val participantes: List<UsuarioSimple>?,
    val materia: MateriaSimple? = null
)

@Serializable
data class UsuarioSimple(
    val id: Int,
    val name: String
)

data class MateriaSimple(
    val id: Int,
    val nombre: String
)

@Serializable
data class CrearSesionRequest(
    val titulo: String,
    val lugar: String,
    val fecha_hora: String,
    val descripcion: String?,
    val materia_id: Int
)