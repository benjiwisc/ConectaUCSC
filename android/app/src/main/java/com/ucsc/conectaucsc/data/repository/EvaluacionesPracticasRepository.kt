package com.ucsc.conectaucsc.data.repository

import com.ucsc.conectaucsc.data.model.*
import com.ucsc.conectaucsc.data.remote.EvaluacionesPracticasApiService
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class EvaluacionesPracticasRepository @Inject constructor(
    private val api: EvaluacionesPracticasApiService
) {
    suspend fun getEvaluaciones(materiaId: Int): Result<List<PracticalEvaluationDto>> {
        return try {
            val response = api.getEvaluaciones(materiaId)
            if (response.isSuccessful) Result.success(response.body() ?: emptyList())
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetalleEvaluacion(evaluationId: Int): Result<PracticalEvaluationDto> {
        return try {
            val response = api.getDetalleEvaluacion(evaluationId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createQuiz(
        materiaId: Int,
        request: CreatePracticalEvaluationRequest
    ): Result<PracticalEvaluationDto> {
        return try {
            val response = api.createQuiz(materiaId, request)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(parseError(response.errorBody())))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQuiz(
        evaluationId: Int,
        respuestas: List<Int>
    ): Result<SubmitQuizResponseDto> {
        return try {
            val response = api.submitQuiz(evaluationId, SubmitPracticalEvaluationRequest(respuestas))
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
