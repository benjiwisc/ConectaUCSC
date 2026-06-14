package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.MateriaRegistro
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RegistroApiService {
    @GET("registro/{usuarioMateriaId}")
    suspend fun getRecord(@Path("usuarioMateriaId") usuarioMateriaId: Int): Response<MateriaRegistro>

    @PUT("registro/{id}/finalizar")
    suspend fun finalizarRecord(@Path("id") id: Int): Response<MateriaRegistro>
}