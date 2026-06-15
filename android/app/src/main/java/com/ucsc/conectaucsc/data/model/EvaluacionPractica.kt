package com.ucsc.conectaucsc.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class EvaluacionPractica(
    val id: Int,
    val materia_id: Int,
    val user_id: Int,
    val titulo: String,
    val descripcion: String,
    val pdf_path: String?,
    val hecha: Boolean = false,
    @SerializedName("creador")
    val creador: User? = null
)