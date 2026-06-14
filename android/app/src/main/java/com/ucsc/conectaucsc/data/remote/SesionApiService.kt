package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.CrearSesionRequest
import com.ucsc.conectaucsc.data.model.SesionEstudio
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE

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
}