package com.ucsc.conectaucsc.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.EvaluacionPractica
import com.ucsc.conectaucsc.data.repository.EvaluacionesPracticasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class EvaluacionesPracticasViewModel @Inject constructor(
    private val repository: EvaluacionesPracticasRepository
) : ViewModel() {

    private val _evaluaciones = MutableStateFlow<List<EvaluacionPractica>>(emptyList())
    val evaluaciones: StateFlow<List<EvaluacionPractica>> = _evaluaciones

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
                _mensaje.value = "Error al cargar: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    fun marcarComoHecha(evaluationId: Int, materiaId: Int) {
        viewModelScope.launch {
            repository.marcarComoHecha(evaluationId).onSuccess {
                cargarEvaluaciones(materiaId)
                _mensaje.value = "Marcada como completada"
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun crearEvaluacion(
        context: Context,
        materiaId: Int,
        titulo: String,
        descripcion: String,
        uri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var pdfPart: MultipartBody.Part? = null
                
                uri?.let {
                    val file = withContext(Dispatchers.IO) {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val tempFile = File(context.cacheDir, getFileName(context, it))
                        val outputStream = FileOutputStream(tempFile)
                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        tempFile
                    }
                    
                    val requestFile = file.readBytes().toRequestBody("application/pdf".toMediaTypeOrNull())
                    pdfPart = MultipartBody.Part.createFormData("pdf", file.name, requestFile)
                }

                repository.createEvaluacion(materiaId, titulo, descripcion, pdfPart).onSuccess {
                    cargarEvaluaciones(materiaId)
                    _mensaje.value = "Evaluación creada correctamente"
                }.onFailure {
                    _mensaje.value = "Error al crear: ${it.message}"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error con el archivo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarEvaluacion(evaluationId: Int, materiaId: Int) {
        viewModelScope.launch {
            repository.eliminarEvaluacion(evaluationId).onSuccess {
                cargarEvaluaciones(materiaId)
                _mensaje.value = "Evaluación eliminada"
            }.onFailure {
                _mensaje.value = it.message
            }
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
                    } catch (e: Exception) {
                        Log.e("Download", "Error saving PDF", e)
                        null
                    }
                }
                
                if (savedFile != null) {
                    _mensaje.value = "PDF descargado"
                    abrirPdf(context, savedFile)
                } else {
                    _mensaje.value = "Error al guardar el archivo"
                }
            }.onFailure {
                _mensaje.value = "Error de descarga: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    private fun abrirPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _mensaje.value = "No hay una aplicación para abrir el PDF"
            Log.e("Download", "Error opening PDF", e)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "archivo.pdf"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}