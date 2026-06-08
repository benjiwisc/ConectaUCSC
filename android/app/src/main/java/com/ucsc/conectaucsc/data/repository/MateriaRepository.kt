package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.Materia
import com.ucsc.conectaucsc.data.remote.MateriaApiService
import javax.inject.Inject

class MateriaRepository @Inject constructor(
    private val api: MateriaApiService
) {
    suspend fun getMateriasPorCarrera(carreraId: Int): Result<List<Materia>> {
        return try {
            val response = api.getMateriasPorCarrera(carreraId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar materias"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getMisMaterias(): Result<List<Materia>> {
        return try {
            val response = api.getMisMaterias()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar tus materias"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun agregarMateria(materiaId: Int): Result<String> {
        return try {
            val response = api.agregarMateria(mapOf("materia_id" to materiaId))
            if (response.isSuccessful) Result.success("Materia agregada")
            else Result.failure(Exception("Ya estás cursando esta materia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun eliminarMateria(materiaId: Int): Result<String> {
        return try {
            val response = api.eliminarMateria(materiaId)
            if (response.isSuccessful) Result.success("Materia eliminada")
            else Result.failure(Exception("Error al eliminar materia"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}