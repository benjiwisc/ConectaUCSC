package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.MateriaRegistro
import com.ucsc.conectaucsc.data.remote.RegistroApiService
import javax.inject.Inject

class RegistroRepository @Inject constructor(private val api: RegistroApiService) {
    suspend fun getRecord(usuarioMateriaId: Int): Result<MateriaRegistro> {
        return try {
            val response = api.getRecord(usuarioMateriaId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar registro"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun finalizarRecord(id: Int): Result<MateriaRegistro> {
        return try {
            val response = api.finalizarRecord(id)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al finalizar registro"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}