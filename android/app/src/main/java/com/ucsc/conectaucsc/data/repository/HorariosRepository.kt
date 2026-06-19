package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.Schedule
import com.ucsc.conectaucsc.data.remote.HorariosApiService
import javax.inject.Inject

class HorariosRepository @Inject constructor(private val api: HorariosApiService){
    suspend fun getTodosHorarios(): Result<List<Schedule>> {
        return try {
            val response = api.getTodosHorarios()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar horarios"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getHorarios(recordId: Int): Result<List<Schedule>> {
        return try {
            val response = api.getHorarios(recordId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar horarios"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun editHorarios(horarioId: Int): Result<Schedule> {
        return try {
            val response = api.editHorarios(horarioId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar horarios"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun createHorario(body: Schedule): Result<Schedule> {
        return try {
            val response = api.createHorario(body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al crear horario"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun updateHorario(id: Int, body: Schedule): Result<Schedule> {
        return try {
            val response = api.updateHorario(id, body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al actualizar horario"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun deleteHorario(id: Int): Result<Map<String, String>> {
        return try {
            val response = api.deleteHorario(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al eliminar horario"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}