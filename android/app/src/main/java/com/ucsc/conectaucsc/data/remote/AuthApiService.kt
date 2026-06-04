package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.AuthResponse
import com.ucsc.conectaucsc.data.model.LoginRequest
import com.ucsc.conectaucsc.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}