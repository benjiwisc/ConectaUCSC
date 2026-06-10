package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.repository.RegistroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val repository: RegistroRepository
) : ViewModel() {

    private val _record = MutableStateFlow<Record?>(null)
    val record: StateFlow<Record?> = _record

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarRecord(usuarioMateriaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRecord(usuarioMateriaId).onSuccess {
                _record.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun finalizarRecord(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.finalizarRecord(id).onSuccess {
                _record.value = it
                _mensaje.value = "Ramo finalizado correctamente"
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}