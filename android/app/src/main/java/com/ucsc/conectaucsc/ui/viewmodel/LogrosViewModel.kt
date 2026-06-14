package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.Attainment
import com.ucsc.conectaucsc.data.repository.LogrosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    private val repository: LogrosRepository
) : ViewModel() {

    private val _logros = MutableStateFlow<List<Attainment>>(emptyList())
    val logros: StateFlow<List<Attainment>> = _logros

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarLogros(recordId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLogros(recordId).onSuccess {
                _logros.value = it
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