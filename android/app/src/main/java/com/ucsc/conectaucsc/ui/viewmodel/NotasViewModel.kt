package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.Grade
import com.ucsc.conectaucsc.data.repository.NotasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotasViewModel @Inject constructor(
    private val repository: NotasRepository
) : ViewModel() {
    private val _notas = MutableStateFlow<List<Grade>>(emptyList())
    val notas: StateFlow<List<Grade>> = _notas

    private val _promedio = MutableStateFlow<Double?>(null)
    val promedio: StateFlow<Double?> = _promedio

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarNotas(recordId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getNotas(recordId).onSuccess {
                _notas.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun createNota(body: Map<String, Any>, recordId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createNota(body).onSuccess {
                _mensaje.value = "Nota agregada correctamente"
                cargarNotas(recordId)
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun updateNota(id: Int, body: Map<String, Any>, recordId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateNota(id, body).onSuccess {
                _mensaje.value = "Nota actualizada correctamente"
                cargarNotas(recordId)
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun deleteNota(id: Int, recordId: Int) {
        viewModelScope.launch {
            repository.deleteNota(id).onSuccess {
                _mensaje.value = "Nota eliminada correctamente"
                cargarNotas(recordId)
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}