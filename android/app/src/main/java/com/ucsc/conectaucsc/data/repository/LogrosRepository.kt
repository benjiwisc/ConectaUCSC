package com.ucsc.conectaucsc.data.repository


import com.ucsc.conectaucsc.data.model.Attainment
import com.ucsc.conectaucsc.data.remote.LogrosApiService

import javax.inject.Inject

class LogrosRepository @Inject constructor(private val api: LogrosApiService) {

    suspend fun getLogros(recordId: Int): Result<List<Attainment>> {
        return try {
            val response = api.getLogros(recordId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar logros"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

}