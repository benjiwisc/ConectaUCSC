package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface EvaluacionesPracticasApiService {
    @GET("materias/{materiaId}/evaluaciones-practicas")
    suspend fun getEvaluaciones(@Path("materiaId") materiaId: Int): Response<List<PracticalEvaluationDto>>

    @GET("evaluaciones-practicas/{evaluationId}")
    suspend fun getDetalleEvaluacion(@Path("evaluationId") evaluationId: Int): Response<PracticalEvaluationDto>

    @POST("materias/{materiaId}/evaluaciones-practicas")
    suspend fun createQuiz(
        @Path("materiaId") materiaId: Int,
        @Body request: CreatePracticalEvaluationRequest
    ): Response<PracticalEvaluationDto>

    @GET("evaluaciones-practicas/{evaluationId}/descargar")
    @Streaming
    suspend fun descargarPdf(@Path("evaluationId") evaluationId: Int): Response<ResponseBody>

    @POST("evaluaciones-practicas/{evaluationId}/hecha")
    suspend fun submitQuiz(
        @Path("evaluationId") evaluationId: Int,
        @Body request: SubmitPracticalEvaluationRequest
    ): Response<SubmitQuizResponseDto>

    @DELETE("evaluaciones-practicas/{evaluationId}")
    suspend fun eliminarEvaluacion(@Path("evaluationId") evaluationId: Int): Response<Unit>
}
