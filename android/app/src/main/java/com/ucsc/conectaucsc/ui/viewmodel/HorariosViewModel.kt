package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.Schedule
import com.ucsc.conectaucsc.data.repository.HorariosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HorariosViewModel @Inject constructor(
    private val repository: HorariosRepository
) : ViewModel() {

    private val _horarios = MutableStateFlow<List<Schedule>>(emptyList())
    val horarios: StateFlow<List<Schedule>> = _horarios

    private val _todosHorarios = MutableStateFlow<List<Schedule>>(emptyList())
    val todosHorarios: StateFlow<List<Schedule>> = _todosHorarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje
    private val _horarioDetalle = MutableStateFlow<Schedule?>(null)
    val horarioDetalle: StateFlow<Schedule?> = _horarioDetalle.asStateFlow()

    fun cargarTodosHorarios() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTodosHorarios().onSuccess {
                _todosHorarios.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun cargarHorarios(recordId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getHorarios(recordId).onSuccess {
                _horarios.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun editHorarios(recordId: Int,horarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.editHorarios(horarioId).onSuccess { horarioRecibido ->
                _horarioDetalle.value = horarioRecibido
            }.onFailure {
                _mensaje.value = it.message ?: "Error al cargar el horario"
            }
            _isLoading.value = false
        }
    }

    fun createHorario(body: Schedule) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createHorario(body).onSuccess {
                _mensaje.value = "Horario creado correctamente"
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun updateHorario(id: Int, body: Schedule) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateHorario(id, body).onSuccess {
                _mensaje.value = "Horario actualizado correctamente"
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun deleteHorario(id: Int, recordId: Int) {
        viewModelScope.launch {
            repository.deleteHorario(id).onSuccess {
                _mensaje.value = "Horario eliminado correctamente"
                cargarHorarios(recordId)
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }


}