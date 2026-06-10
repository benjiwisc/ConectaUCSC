package com.ucsc.conectaucsc.data.remote


import com.ucsc.conectaucsc.data.model.Attendance
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AsistenciasApiService {
    @POST("asistencias/registrar")
    suspend fun registrarAsistencia(@Body body: Map<String, Any>): Response<List<Attendance>>

    @GET("asistencias/{recordId}")
    suspend fun getAsistencias(@Path("recordId") recordId: Int): Response<List<Attendance>>

    @POST("asistencias")
    suspend fun createAsistencia(@Body body: Map<String, Any>): Response<Attendance>

    @PUT("asistencias/{id}")
    suspend fun updateAsistencia(@Path("id") id: Int, @Body body: Map<String, Any>): Response<Attendance>

    @PUT("asistencias/{id}/justificar")
    suspend fun justificarAsistencia(@Path("id") id: Int): Response<Attendance>

    @DELETE("asistencias/{id}")
    suspend fun deleteAsistencia(@Path("id") id: Int): Response<Map<String, String>>

}