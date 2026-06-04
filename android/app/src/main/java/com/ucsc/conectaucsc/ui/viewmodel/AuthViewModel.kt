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

    fun register(name: String, email: String, password: String, carrera: String?, facultad: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(name, email, password, carrera, facultad)
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