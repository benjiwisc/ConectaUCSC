package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.AuthResponse
import com.ucsc.conectaucsc.data.model.LoginRequest
import com.ucsc.conectaucsc.data.model.RegisterRequest
import com.ucsc.conectaucsc.data.remote.AuthApiService
import com.ucsc.conectaucsc.data.model.Carrera
import com.ucsc.conectaucsc.data.model.Facultad
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
                val errorMsg = try {
                    val errorJson = response.errorBody()?.string() ?: ""
                    val jsonObject = org.json.JSONObject(errorJson)
                    jsonObject.optString("message", "Credenciales incorrectas")
                } catch (e: Exception) {
                    "Credenciales incorrectas"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getFacultades(): Result<List<Facultad>> {
        return try {
            val response = api.getFacultades()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar facultades"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getCarreras(facultadId: Int): Result<List<Carrera>> {
        return try {
            val response = api.getCarreras(facultadId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar carreras"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        facultadId: Int?,
        carreraId: Int?
    ): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password, facultadId, carreraId))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = try {
                    val errorJson = response.errorBody()?.string() ?: ""
                    val jsonObject = org.json.JSONObject(errorJson)
                    jsonObject.optString("message", "Error al registrar usuario")
                } catch (e: Exception) {
                    "Error al registrar usuario"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getMe(): Result<com.ucsc.conectaucsc.data.model.User> {
        return try {
            val response = api.getMe()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar datos de perfil"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getUserStats(): Result<com.ucsc.conectaucsc.data.model.UserStats> {
        return try {
            val response = api.getUserStats()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar estadísticas"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}