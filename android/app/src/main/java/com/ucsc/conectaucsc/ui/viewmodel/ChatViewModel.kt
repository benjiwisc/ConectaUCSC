package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.MensajeChat
import com.ucsc.conectaucsc.data.repository.SesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: SesionRepository
) : ViewModel() {

    private val _mensajes = MutableStateFlow<List<MensajeChat>>(emptyList())
    val mensajes: StateFlow<List<MensajeChat>> = _mensajes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun cargarMensajes(sesionId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMensajes(sesionId).onSuccess {
                _mensajes.value = it
            }.onFailure {
                _mensajeError.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun enviarMensaje(sesionId: Int, contenido: String) {
        if (contenido.isBlank()) return

        viewModelScope.launch {
            repository.enviarMensaje(sesionId, contenido).onSuccess { nuevoMensaje ->
                
                val listaActualizada = _mensajes.value.toMutableList()
                listaActualizada.add(nuevoMensaje)
                _mensajes.value = listaActualizada
                
                
                cargarMensajes(sesionId)
            }.onFailure {
                _mensajeError.value = it.message
            }
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }
}