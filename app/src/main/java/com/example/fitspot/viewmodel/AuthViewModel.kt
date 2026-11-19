package com.example.fitspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitspot.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

data class ResetStatus(val message: String, val isError: Boolean)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isLoggedIn = repository.isLoggedIn))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) = 
        authenticate { repository.signIn(email, password) }

    fun signUp(email: String, password: String) = 
        authenticate { repository.signUp(email, password) }
    
    val userEmail: String?
        get() = repository.currentEmail

    private val _resetStatus = MutableStateFlow<ResetStatus?>(null)
    val resetStatus: StateFlow<ResetStatus?> = _resetStatus.asStateFlow()

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            repository.sendPasswordReset(email)
                .onSuccess {
                    _resetStatus.value = ResetStatus(
                        "If an account exists for $email, a reset link has been sent.", isError = false
                    )
                }
                .onFailure { e ->
                    _resetStatus.value = ResetStatus(
                        e.message ?: "Could not send reset email", isError = true
                    )
                }
        }
    }

    fun clearResetStatus() {
        _resetStatus.value = null
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    private fun authenticate(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            action()
                .onSuccess { _uiState.value = AuthUiState(isLoggedIn = true) }
                .onFailure { e -> 
                    _uiState.value = AuthUiState(error = e.message ?: "Authentication failed")
                }
        }
    }
}
