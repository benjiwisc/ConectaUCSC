package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.Attainment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LogrosApiService {

    @GET("logros/{recordId}")
    suspend fun getLogros(@Path("recordId") recordId: Int): Response<List<Attainment>>
}