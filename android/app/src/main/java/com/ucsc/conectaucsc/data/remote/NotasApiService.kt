package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.Grade
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotasApiService {
    @GET("notas/{recordId}")
    suspend fun getNotas(@Path("recordId") recordId: Int): Response<List<Grade>>

    @GET("notas/editar/{recordId}")
    suspend fun editNotas(@Path("recordId") recordId: Int): Response<Grade>

    @POST("notas")
    suspend fun createNota(@Body body: Grade): Response<Grade>

    @PUT("notas/{id}")
    suspend fun updateNota(@Path("id") id: Int, @Body body: Grade): Response<Grade>

    @DELETE("notas/{id}")
    suspend fun deleteNota(@Path("id") id: Int): Response<Map<String, String>>

    @GET("notas/{recordId}/promedio")
    suspend fun getPromedio(@Path("recordId") recordId: Int): Response<Grade>

}