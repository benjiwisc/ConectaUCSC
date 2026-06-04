package com.ucsc.conectaucsc.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ucsc.conectaucsc.data.model.AuthResponse
import com.ucsc.conectaucsc.data.repository.AuthRepository
import com.ucsc.conectaucsc.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager(application)

    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
}