package com.ucsc.conectaucsc.data.model

data class AuthResponse(
    val token: String,
    val user: User
)