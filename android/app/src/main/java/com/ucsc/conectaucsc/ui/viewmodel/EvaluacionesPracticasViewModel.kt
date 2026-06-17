package com.ucsc.conectaucsc.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.*
import com.ucsc.conectaucsc.data.repository.EvaluacionesPracticasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class EvaluacionesPracticasViewModel @Inject constructor(
    private val repository: EvaluacionesPracticasRepository
) : ViewModel() {

    private val _evaluaciones = MutableStateFlow<List<PracticalEvaluationDto>>(emptyList())
    val evaluaciones: StateFlow<List<PracticalEvaluationDto>> = _evaluaciones

    private val _evaluationActual = MutableStateFlow<PracticalEvaluationDto?>(null)
    val evaluationActual: StateFlow<PracticalEvaluationDto?> = _evaluationActual

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarEvaluaciones(materiaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEvaluaciones(materiaId).onSuccess {
                _evaluaciones.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun cargarDetalleEvaluacion(evaluationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDetalleEvaluacion(evaluationId).onSuccess {
                _evaluationActual.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun crearCuestionario(materiaId: Int, request: CreatePracticalEvaluationRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createQuiz(materiaId, request).onSuccess {
                _mensaje.value = "Cuestionario creado con éxito"
                cargarEvaluaciones(materiaId)
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun enviarRespuestas(evaluationId: Int, respuestas: List<Int>) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitQuiz(evaluationId, respuestas).onSuccess { response ->
                _evaluationActual.value = response.evaluation.copy(
                    completion = response.completion,
                    hecha = true
                )
                _mensaje.value = "Evaluación enviada con éxito"
            }.onFailure {
                _mensaje.value = "Error al enviar: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    fun eliminarEvaluacion(evaluationId: Int, materiaId: Int) {
        viewModelScope.launch {
            repository.eliminarEvaluacion(evaluationId).onSuccess {
                cargarEvaluaciones(materiaId)
                _mensaje.value = "Evaluación eliminada"
            }.onFailure { _mensaje.value = it.message }
        }
    }

    fun descargarPdf(context: Context, evaluationId: Int, fileName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.descargarPdf(evaluationId).onSuccess { responseBody ->
                val savedFile = withContext(Dispatchers.IO) {
                    try {
                        val file = File(context.getExternalFilesDir(null), fileName)
                        responseBody.byteStream().use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        file
                    } catch (e: Exception) { null }
                }
                savedFile?.let { abrirPdf(context, it) } ?: run { _mensaje.value = "Error al guardar PDF" }
            }.onFailure { _mensaje.value = it.message }
            _isLoading.value = false
        }
    }

    private fun abrirPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _mensaje.value = "No hay lector de PDF instalado"
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }
}
