package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.Materia
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MateriaApiService {
    @GET("carreras/{id}/materias")
    suspend fun getMateriasPorCarrera(@Path("id") carreraId: Int): Response<List<Materia>>

    @GET("mis-materias")
    suspend fun getMisMaterias(): Response<List<Materia>>

    @POST("mis-materias")
    suspend fun agregarMateria(@Body body: Map<String, Int>): Response<Map<String, String>>

    @DELETE("mis-materias/{materiaId}")
    suspend fun eliminarMateria(@Path("materiaId") materiaId: Int): Response<Map<String, String>>
}