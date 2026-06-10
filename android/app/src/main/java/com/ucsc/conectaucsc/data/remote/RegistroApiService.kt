package com.ucsc.conectaucsc.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RegistroApiService {
    @GET("registro/{usuarioMateriaId}")
    suspend fun getRecord(@Path("usuarioMateriaId") usuarioMateriaId: Int): Response<Record>

    @PUT("registro/{id}/finalizar")
    suspend fun finalizarRecord(@Path("id") id: Int): Response<Record>
}