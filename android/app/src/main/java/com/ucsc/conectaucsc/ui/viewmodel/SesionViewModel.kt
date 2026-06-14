package com.ucsc.conectaucsc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.CrearSesionRequest
import com.ucsc.conectaucsc.data.model.SesionEstudio
import com.ucsc.conectaucsc.data.repository.SesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SesionViewModel @Inject constructor(
    private val repository: SesionRepository
) : ViewModel() {

    private val _sesiones = MutableStateFlow<List<SesionEstudio>>(emptyList())
    val sesiones: StateFlow<List<SesionEstudio>> = _sesiones

    private val _misSesiones = MutableStateFlow<List<SesionEstudio>>(emptyList())
    val misSesiones: StateFlow<List<SesionEstudio>> = _misSesiones

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _sesionCreada = MutableStateFlow(false)
    val sesionCreada: StateFlow<Boolean> = _sesionCreada

    fun cargarSesionesPorMateria(materiaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSesionesPorMateria(materiaId).onSuccess {
                _sesiones.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun crearSesion(
        titulo: String,
        lugar: String,
        fechaHora: String,
        descripcion: String?,
        materiaId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val request = CrearSesionRequest(titulo, lugar, fechaHora, descripcion, materiaId)
            repository.crearSesion(request).onSuccess {
                _mensaje.value = "Sesión creada correctamente"
                _sesionCreada.value = true
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun cargarTodasSesiones(buscar: String? = null, orden: String? = "recientes") {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTodasSesiones(buscar, orden).onSuccess {
                _sesiones.value = it
            }.onFailure {
                _mensaje.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun unirse(sesionId: Int, materiaId: Int, onGeneral: Boolean = false, buscar: String? = null, orden: String? = null) {
        viewModelScope.launch {
            repository.unirse(sesionId).onSuccess {
                _mensaje.value = it
                if (onGeneral) {
                    cargarTodasSesiones(buscar, orden)
                } else {
                    cargarSesionesPorMateria(materiaId)
                }
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun cargarMisSesiones() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMisSesiones().onSuccess {
                _misSesiones.value = it
            }
            _isLoading.value = false
        }
    }

    fun salirse(sesionId: Int, materiaId: Int, onGeneral: Boolean = false, buscar: String? = null, orden: String? = null) {
        viewModelScope.launch {
            repository.salirse(sesionId).onSuccess {
                _mensaje.value = it
                if (onGeneral) {
                    cargarTodasSesiones(buscar, orden)
                } else {
                    cargarSesionesPorMateria(materiaId)
                }
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun finalizar(sesionId: Int, materiaId: Int, onGeneral: Boolean = false, buscar: String? = null, orden: String? = null) {
        viewModelScope.launch {
            repository.finalizar(sesionId).onSuccess {
                _mensaje.value = it
                if (onGeneral) {
                    cargarTodasSesiones(buscar, orden)
                } else {
                    cargarSesionesPorMateria(materiaId)
                }
            }.onFailure {
                _mensaje.value = it.message
            }
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }
    fun limpiarSesionCreada() { _sesionCreada.value = false }
}