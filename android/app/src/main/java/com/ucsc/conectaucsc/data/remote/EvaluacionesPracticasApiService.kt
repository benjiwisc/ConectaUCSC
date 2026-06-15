package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.EvaluacionPractica
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface EvaluacionesPracticasApiService {
    @GET("materias/{materiaId}/evaluaciones-practicas")
    suspend fun getEvaluaciones(@Path("materiaId") materiaId: Int): Response<List<EvaluacionPractica>>

    @Multipart
    @POST("materias/{materiaId}/evaluaciones-practicas")
    suspend fun createEvaluacion(
        @Path("materiaId") materiaId: Int,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part pdf: MultipartBody.Part?
    ): Response<EvaluacionPractica>

    @GET("evaluaciones-practicas/{evaluationId}/descargar")
    @Streaming
    suspend fun descargarPdf(@Path("evaluationId") evaluationId: Int): Response<ResponseBody>

    @POST("evaluaciones-practicas/{evaluationId}/hecha")
    suspend fun marcarComoHecha(@Path("evaluationId") evaluationId: Int): Response<EvaluacionPractica>

    @DELETE("evaluaciones-practicas/{evaluationId}")
    suspend fun eliminarEvaluacion(@Path("evaluationId") evaluationId: Int): Response<Unit>
}