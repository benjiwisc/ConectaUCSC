package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.AuthResponse
import com.ucsc.conectaucsc.data.model.LoginRequest
import com.ucsc.conectaucsc.data.model.RegisterRequest
import com.ucsc.conectaucsc.data.remote.AuthApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApiService
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        carrera: String?,
        facultad: String?
    ): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password, carrera, facultad))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error ${response.code()}: $errorBody")) // ← más detalle
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}")) // ← más detalle
        }
    }
}