package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.Attendance
import com.ucsc.conectaucsc.data.repository.AsistenciasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AsistenciasViewModel @Inject constructor(
    private val repository: AsistenciasRepository
) : ViewModel() {
    private val _asistencias = MutableStateFlow<List<Attendance>>(emptyList())
    val asistencias: StateFlow<List<Attendance>> = _asistencias

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarAsistencias(recordId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAsistencias(recordId).onSuccess {
                _asistencias.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun registrarAsistencia(latitud: Double, longitud: Double, fecha: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.registrarAsistencia(mapOf(
                "latitud" to latitud,
                "longitud" to longitud,
                "fecha" to fecha
            )).onSuccess {
                _mensaje.value = "Asistencia registrada correctamente"
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun justificarAsistencia(id: Int, recordId: Int) {
        viewModelScope.launch {
            repository.justificarAsistencia(id).onSuccess {
                _mensaje.value = "Asistencia justificada correctamente"
                cargarAsistencias(recordId)
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun deleteAsistencia(id: Int, recordId: Int) {
        viewModelScope.launch {
            repository.deleteAsistencia(id).onSuccess {
                _mensaje.value = "Asistencia eliminada correctamente"
                cargarAsistencias(recordId)
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

}