package com.ucsc.conectaucsc.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MensajeChat(
    val id: Int? = null,
    val sesion_id: Int,
    val user_id: Int,
    @SerializedName("message")
    val mensaje: String,
    val created_at: String? = null,
    val user: UsuarioSimple? = null
)

@Serializable
data class EnviarMensajeRequest(
    @SerializedName("message")
    val mensaje: String
)