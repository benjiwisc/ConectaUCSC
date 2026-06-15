package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.CrearSesionRequest
import com.ucsc.conectaucsc.data.model.SesionEstudio
import com.ucsc.conectaucsc.data.model.MensajeChat
import com.ucsc.conectaucsc.data.model.EnviarMensajeRequest
import retrofit2.Response
import retrofit2.http.*

interface SesionApiService {
    @GET("materias/{id}/sesiones")
    suspend fun getSesionesPorMateria(@Path("id") materiaId: Int): Response<List<SesionEstudio>>

    @POST("sesiones")
    suspend fun crearSesion(@Body request: CrearSesionRequest): Response<SesionEstudio>

    @POST("sesiones/{id}/unirse")
    suspend fun unirse(@Path("id") sesionId: Int): Response<Map<String, String>>

    @GET("mis-sesiones")
    suspend fun getMisSesiones(): Response<List<SesionEstudio>>

    @DELETE("sesiones/{id}/salirse")
    suspend fun salirse(@Path("id") sesionId: Int): Response<Map<String, String>>

    @DELETE("sesiones/{id}/finalizar")
    suspend fun finalizar(@Path("id") sesionId: Int): Response<Map<String, String>>

    @GET("sesiones")
    suspend fun getTodasSesiones(
        @Query("buscar") buscar: String?,
        @Query("orden") orden: String?
    ): Response<List<SesionEstudio>>

    @GET("sesiones/{id}/mensajes")
    suspend fun getMensajes(@Path("id") sesionId: Int): Response<List<MensajeChat>>

    @POST("sesiones/{id}/mensajes")
    suspend fun enviarMensaje(
        @Path("id") sesionId: Int,
        @Body request: EnviarMensajeRequest
    ): Response<MensajeChat>
}