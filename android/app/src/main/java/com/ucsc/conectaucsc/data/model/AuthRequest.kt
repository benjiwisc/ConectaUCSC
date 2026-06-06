package com.ucsc.conectaucsc.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val facultad_id: Int?,
    val carrera_id: Int?
)