package com.ucsc.conectaucsc.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AlternativeDto(
    @SerializedName("texto") val texto: String,
    @SerializedName("correcta") val correcta: Boolean? = null
)

@Serializable
data class QuestionDto(
    @SerializedName("enunciado") val enunciado: String,
    @SerializedName("alternativas") val alternativas: List<AlternativeDto>
)

@Serializable
data class PracticalEvaluationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("materia_id") val materia_id: Int,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("contenido") val contenido: List<QuestionDto>? = null,
    @SerializedName("creador") val creador: UsuarioSimple? = null,
    @SerializedName("hecha") val hecha: Boolean = false,
    @SerializedName("nota") val nota: Double? = null,
    @SerializedName("puntaje") val puntaje: Double? = null,
    @SerializedName("correctas") val correctas: Int? = null,
    @SerializedName("total_preguntas") val total_preguntas: Int? = null,
    @SerializedName("completed_at") val completed_at: String? = null,
    @SerializedName("completion") val completion: PracticalEvaluationCompletionDto? = null
)

@Serializable
data class PracticalEvaluationCompletionDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nota") val nota: Double? = null,
    @SerializedName("puntaje") val puntaje: Double? = null,
    @SerializedName("correctas") val correctas: Int? = null,
    @SerializedName("total_preguntas") val total_preguntas: Int? = null,
    @SerializedName("respuestas") val respuestas: List<Int>? = null,
    @SerializedName("completed_at") val completed_at: String? = null
)

@Serializable
data class SubmitQuizResponseDto(
    @SerializedName("evaluation") val evaluation: PracticalEvaluationDto,
    @SerializedName("completion") val completion: PracticalEvaluationCompletionDto
)

@Serializable
data class CreatePracticalEvaluationRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("preguntas") val preguntas: List<QuestionDto>,
    @SerializedName("tipo") val tipo: String = "quiz"
)

@Serializable
data class SubmitPracticalEvaluationRequest(
    @SerializedName("respuestas") val respuestas: List<Int>
)
