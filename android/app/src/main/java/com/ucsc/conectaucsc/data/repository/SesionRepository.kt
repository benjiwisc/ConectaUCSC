package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.CrearSesionRequest
import com.ucsc.conectaucsc.data.model.SesionEstudio
import com.ucsc.conectaucsc.data.remote.SesionApiService
import javax.inject.Inject

class SesionRepository @Inject constructor(
    private val api: SesionApiService
) {
    suspend fun getSesionesPorMateria(materiaId: Int): Result<List<SesionEstudio>> {
        return try {
            val response = api.getSesionesPorMateria(materiaId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar sesiones"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun crearSesion(request: CrearSesionRequest): Result<SesionEstudio> {
        return try {
            val response = api.crearSesion(request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al crear sesión"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun unirse(sesionId: Int): Result<String> {
        return try {
            val response = api.unirse(sesionId)
            if (response.isSuccessful) Result.success("Te uniste a la sesión")
            else Result.failure(Exception("Ya eres participante de esta sesión"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getMisSesiones(): Result<List<SesionEstudio>> {
        return try {
            val response = api.getMisSesiones()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar tus sesiones"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun salirse(sesionId: Int): Result<String> {
        return try {
            val response = api.salirse(sesionId)
            if (response.isSuccessful) Result.success("Has abandonado la sesión")
            else Result.failure(Exception("Error al abandonar la sesión"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun finalizar(sesionId: Int): Result<String> {
        return try {
            val response = api.finalizar(sesionId)
            if (response.isSuccessful) Result.success("Sesión finalizada correctamente")
            else Result.failure(Exception("Error al finalizar la sesión"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}