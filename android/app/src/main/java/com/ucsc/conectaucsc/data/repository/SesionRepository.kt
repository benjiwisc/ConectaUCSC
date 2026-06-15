package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.CrearSesionRequest
import com.ucsc.conectaucsc.data.model.SesionEstudio
import com.ucsc.conectaucsc.data.model.MensajeChat
import com.ucsc.conectaucsc.data.model.EnviarMensajeRequest
import com.ucsc.conectaucsc.data.remote.SesionApiService
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class SesionRepository @Inject constructor(
    private val api: SesionApiService
) {
    suspend fun getSesionesPorMateria(materiaId: Int): Result<List<SesionEstudio>> {
        return try {
            val response = api.getSesionesPorMateria(materiaId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun crearSesion(request: CrearSesionRequest): Result<SesionEstudio> {
        return try {
            val response = api.crearSesion(request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun unirse(sesionId: Int): Result<String> {
        return try {
            val response = api.unirse(sesionId)
            if (response.isSuccessful) Result.success("Te uniste a la sesión")
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getMisSesiones(): Result<List<SesionEstudio>> {
        return try {
            val response = api.getMisSesiones()
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun salirse(sesionId: Int): Result<String> {
        return try {
            val response = api.salirse(sesionId)
            if (response.isSuccessful) Result.success("Has abandonado la sesión")
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun finalizar(sesionId: Int): Result<String> {
        return try {
            val response = api.finalizar(sesionId)
            if (response.isSuccessful) Result.success("Sesión finalizada correctamente")
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getMensajes(sesionId: Int): Result<List<MensajeChat>> {
        return try {
            val response = api.getMensajes(sesionId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun enviarMensaje(sesionId: Int, mensaje: String): Result<MensajeChat> {
        return try {
            val response = api.enviarMensaje(sesionId, EnviarMensajeRequest(mensaje))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getTodasSesiones(buscar: String?, orden: String?): Result<List<SesionEstudio>> {
        return try {
            val response = api.getTodasSesiones(buscar, orden)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al cargar las tutorías"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    private fun parseError(errorBody: ResponseBody?): String {
        return try {
            val json = JSONObject(errorBody?.string() ?: "{}")
            json.optString("message", "Error desconocido en el servidor")
        } catch (e: Exception) {
            "Error al procesar respuesta"
        }
    }
}