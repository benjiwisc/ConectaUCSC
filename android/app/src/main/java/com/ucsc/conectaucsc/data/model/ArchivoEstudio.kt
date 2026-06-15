package com.ucsc.conectaucsc.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ArchivoEstudio(
    val id: Int,
    val materia_id: Int,
    val user_id: Int,
    val titulo: String,
    val descripcion: String,
    val file_path: String?,
    val file_name: String?,
    val file_extension: String?,
    val mime_type: String?,
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("creador")
    val creador: User? = null
) {
    // Función para obtener el nombre mostrando "Mí" si el ID coincide
    fun getNombreMostrar(currentUserId: Int, currentUserName: String): String {
        return if (user_id == currentUserId) {
            "$currentUserName (Tú)"
        } else {
            user?.name ?: creador?.name ?: "Alumno #$user_id"
        }
    }
}