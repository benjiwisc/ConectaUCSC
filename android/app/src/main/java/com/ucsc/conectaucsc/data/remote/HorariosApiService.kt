package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.Schedule
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HorariosApiService {

    @GET("horarios/{recordId}")
    suspend fun getHorarios(@Path("recordId") recordId: Int): Response<List<Schedule>>

    @POST("horarios")
    suspend fun createHorario(@Body body: Map<String, Any>): Response<Schedule>

    @PUT("horarios/{id}")
    suspend fun updateHorario(@Path("id") id: Int, @Body body: Map<String, Any>): Response<Schedule>

    @DELETE("horarios/{id}")
    suspend fun deleteHorario(@Path("id") id: Int): Response<Map<String, String>>

}