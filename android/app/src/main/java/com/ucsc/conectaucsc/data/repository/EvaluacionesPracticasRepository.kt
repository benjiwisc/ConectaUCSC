package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.EvaluacionPractica
import com.ucsc.conectaucsc.data.remote.EvaluacionesPracticasApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class EvaluacionesPracticasRepository @Inject constructor(
    private val api: EvaluacionesPracticasApiService
) {
    suspend fun getEvaluaciones(materiaId: Int): Result<List<EvaluacionPractica>> {
        return try {
            val response = api.getEvaluaciones(materiaId)
            if (response.isSuccessful) Result.success(response.body() ?: emptyList())
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvaluacion(
        materiaId: Int,
        titulo: String,
        descripcion: String,
        pdf: MultipartBody.Part?
    ): Result<EvaluacionPractica> {
        return try {
            val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
            val descripcionBody = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.createEvaluacion(materiaId, tituloBody, descripcionBody, pdf)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarComoHecha(evaluationId: Int): Result<EvaluacionPractica> {
        return try {
            val response = api.marcarComoHecha(evaluationId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarPdf(evaluationId: Int): Result<ResponseBody> {
        return try {
            val response = api.descargarPdf(evaluationId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Error al descargar PDF"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarEvaluacion(evaluationId: Int): Result<Unit> {
        return try {
            val response = api.eliminarEvaluacion(evaluationId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(parseError(response.errorBody())))
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