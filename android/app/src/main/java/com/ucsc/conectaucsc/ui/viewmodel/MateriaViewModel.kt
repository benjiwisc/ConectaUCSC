package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.Materia
import com.ucsc.conectaucsc.data.repository.MateriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MateriaViewModel @Inject constructor(
    private val repository: MateriaRepository
) : ViewModel() {

    private val _misMaterias = MutableStateFlow<List<Materia>>(emptyList())
    val misMaterias: StateFlow<List<Materia>> = _misMaterias

    private val _materiasDisponibles = MutableStateFlow<List<Materia>>(emptyList())
    val materiasDisponibles: StateFlow<List<Materia>> = _materiasDisponibles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarMisMaterias() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMisMaterias().onSuccess {
                _misMaterias.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun cargarMateriasDisponibles(carreraId: Int) {
        viewModelScope.launch {
            repository.getMateriasPorCarrera(carreraId).onSuccess {
                _materiasDisponibles.value = it
            }
        }
    }

    fun agregarMateria(materiaId: Int) {
        viewModelScope.launch {
            repository.agregarMateria(materiaId).onSuccess {
                _mensaje.value = it
                cargarMisMaterias()
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun eliminarMateria(materiaId: Int) {
        viewModelScope.launch {
            repository.eliminarMateria(materiaId).onSuccess {
                _mensaje.value = it
                cargarMisMaterias()
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}