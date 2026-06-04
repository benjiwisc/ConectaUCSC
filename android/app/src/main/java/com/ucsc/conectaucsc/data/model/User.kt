package com.ucsc.conectaucsc.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val carrera: String?,
    val facultad: String?
)