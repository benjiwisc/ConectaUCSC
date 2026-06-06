package com.ucsc.conectaucsc.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.AuthResponse
import com.ucsc.conectaucsc.data.repository.AuthRepository
import com.ucsc.conectaucsc.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.ucsc.conectaucsc.data.model.Carrera
import com.ucsc.conectaucsc.data.model.Facultad
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val repository: AuthRepository
) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _authResult = MutableStateFlow<Result<AuthResponse>?>(null)
    val authResult: StateFlow<Result<AuthResponse>?> = _authResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(email, password)
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                sessionManager.saveSession(data.token, data.user.id, data.user.name)
            }
            _authResult.value = result
            _isLoading.value = false
        }
    }

    private val _facultades = MutableStateFlow<List<Facultad>>(emptyList())
    val facultades: StateFlow<List<Facultad>> = _facultades

    private val _carreras = MutableStateFlow<List<Carrera>>(emptyList())
    val carreras: StateFlow<List<Carrera>> = _carreras


    fun loadFacultades() {
        viewModelScope.launch {
            repository.getFacultades().onSuccess { _facultades.value = it }
        }
    }

    fun loadCarreras(facultadId: Int) {
        viewModelScope.launch {
            _carreras.value = emptyList()
            repository.getCarreras(facultadId).onSuccess { _carreras.value = it }
        }
    }

    fun register(name: String, email: String, password: String, facultadId: Int?, carreraId: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(name, email, password, facultadId, carreraId)
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                sessionManager.saveSession(data.token, data.user.id, data.user.name)
            }
            _authResult.value = result
            _isLoading.value = false
        }
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getUserName(): String = sessionManager.getUserName() ?: "Usuario"

    fun logout() {
        sessionManager.clearSession()
        _authResult.value = null
    }
}