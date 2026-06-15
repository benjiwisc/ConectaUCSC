package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.ArchivoEstudio
import com.ucsc.conectaucsc.data.remote.ArchivosEstudioApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class ArchivosEstudioRepository @Inject constructor(
    private val api: ArchivosEstudioApiService
) {
    suspend fun getArchivos(materiaId: Int): Result<List<ArchivoEstudio>> {
        return try {
            val response = api.getArchivos(materiaId)
            if (response.isSuccessful) Result.success(response.body() ?: emptyList())
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadArchivo(
        materiaId: Int,
        titulo: String,
        descripcion: String,
        file: MultipartBody.Part
    ): Result<ArchivoEstudio> {
        return try {
            val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
            val descripcionBody = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.uploadArchivo(materiaId, tituloBody, descripcionBody, file)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarArchivo(fileId: Int): Result<ResponseBody> {
        return try {
            val response = api.descargarArchivo(fileId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al descargar archivo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarArchivo(fileId: Int): Result<Unit> {
        return try {
            val response = api.eliminarArchivo(fileId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al eliminar archivo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(errorBody: ResponseBody?): String {
        return try {
            val json = JSONObject(errorBody?.string() ?: "{}")
            json.optString("message", "Error desconocido en el servidor")
        } catch (e: Exception) {
            "Error al procesar respuesta del servidor"
        }
    }
}