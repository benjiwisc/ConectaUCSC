package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.Grade
import com.ucsc.conectaucsc.data.remote.NotasApiService
import javax.inject.Inject

class NotasRepository  @Inject constructor(private val api: NotasApiService){
    suspend fun getNotas(recordId: Int): Result<List<Grade>> {
        return try {
            val response = api.getNotas(recordId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar notas"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun editNotas(recordId: Int): Result<Grade> {
        return try {
            val response = api.editNotas(recordId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar nota"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun createNota(body: Grade): Result<Grade> {
        return try {
            val response = api.createNota(body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al crear nota"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun updateNota(id: Int, body: Grade): Result<Grade> {
        return try {
            val response = api.updateNota(id, body)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al actualizar nota"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun deleteNota(id: Int): Result<Map<String, String>> {
        return try {
            val response = api.deleteNota(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al eliminar nota"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}