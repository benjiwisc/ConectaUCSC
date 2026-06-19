package com.ucsc.conectaucsc.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val carrera_id: Int?,
    val facultad_id: Int?,
    val carrera: CarreraUserRelation? = null,
    val facultad: FacultadUserRelation? = null
)

@Serializable
data class CarreraUserRelation(
    val id: Int,
    val nombre: String
)

@Serializable
data class FacultadUserRelation(
    val id: Int,
    val nombre: String
)