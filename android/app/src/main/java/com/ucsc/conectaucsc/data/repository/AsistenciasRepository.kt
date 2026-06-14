package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.Attendance
import com.ucsc.conectaucsc.data.remote.AsistenciasApiService
import javax.inject.Inject

class AsistenciasRepository @Inject constructor(private val api: AsistenciasApiService) {
    suspend fun registrarAsistencia(body: Map<String, Any>): Result<List<Attendance>> {
        return try {
            val response = api.registrarAsistencia(body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al registrar asistencia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getAsistencias(recordId: Int): Result<List<Attendance>> {
        return try {
            val response = api.getAsistencias(recordId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar asistencias"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun createAsistencia(body: Map<String, Any>): Result<Attendance> {
        return try {
            val response = api.createAsistencia(body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al crear asistencia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun updateAsistencia(id: Int, body: Map<String, Any>): Result<Attendance> {
        return try {
            val response = api.updateAsistencia(id, body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al actualizar asistencia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun justificarAsistencia(id: Int): Result<Attendance> {
        return try {
            val response = api.justificarAsistencia(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al justificar asistencia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun deleteAsistencia(id: Int): Result<Map<String, String>> {
        return try {
            val response = api.deleteAsistencia(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al eliminar asistencia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}