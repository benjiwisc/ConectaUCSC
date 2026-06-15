package com.ucsc.conectaucsc.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.ArchivoEstudio
import com.ucsc.conectaucsc.data.repository.ArchivosEstudioRepository
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
class ArchivosEstudioViewModel @Inject constructor(
    private val repository: ArchivosEstudioRepository
) : ViewModel() {

    private val _archivos = MutableStateFlow<List<ArchivoEstudio>>(emptyList())
    val archivos: StateFlow<List<ArchivoEstudio>> = _archivos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarArchivos(materiaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getArchivos(materiaId).onSuccess {
                _archivos.value = it
            }.onFailure {
                _mensaje.value = "Error al cargar archivos: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    fun subirArchivo(
        context: Context,
        materiaId: Int,
        titulo: String,
        descripcion: String,
        uri: Uri
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fileData = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
                    val fileName = getFileName(context, uri)
                    
                    val allowedExtensions = listOf("pdf", "doc", "docx", "ppt", "pptx")
                    val extension = fileName.substringAfterLast(".", "").lowercase()
                    
                    if (!allowedExtensions.contains(extension)) {
                        throw Exception("Formato no permitido. Solo PDF, Word o PowerPoint.")
                    }

                    val bytes = inputStream?.readBytes() ?: throw Exception("No se pudo leer el archivo")
                    Triple(bytes, fileName, mimeType)
                }

                val requestFile = fileData.first.toRequestBody(fileData.third.toMediaTypeOrNull())
                // CAMBIO: Se cambió "file" por "archivo" para coincidir con el backend de Laravel
                val body = MultipartBody.Part.createFormData("archivo", fileData.second, requestFile)

                repository.uploadArchivo(materiaId, titulo, descripcion, body).onSuccess {
                    cargarArchivos(materiaId)
                    _mensaje.value = "Archivo subido correctamente"
                }.onFailure {
                    _mensaje.value = "Error al subir: ${it.message}"
                }
            } catch (e: Exception) {
                _mensaje.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarArchivo(fileId: Int, materiaId: Int) {
        viewModelScope.launch {
            repository.eliminarArchivo(fileId).onSuccess {
                cargarArchivos(materiaId)
                _mensaje.value = "Archivo eliminado"
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun descargarArchivo(context: Context, fileId: Int, fileName: String, mimeType: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.descargarArchivo(fileId).onSuccess { responseBody ->
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
                        null
                    }
                }
                
                if (savedFile != null) {
                    _mensaje.value = "Archivo descargado"
                    abrirArchivo(context, savedFile, mimeType ?: "application/octet-stream")
                } else {
                    _mensaje.value = "Error al guardar el archivo"
                }
            }.onFailure {
                _mensaje.value = "Error de descarga: ${it.message}"
            }
            _isLoading.value = false
        }
    }

    private fun abrirArchivo(context: Context, file: File, mimeType: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _mensaje.value = "No hay una aplicación para abrir este archivo"
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "archivo"
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