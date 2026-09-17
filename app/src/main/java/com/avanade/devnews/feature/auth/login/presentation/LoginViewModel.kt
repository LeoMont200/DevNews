package com.avanade.devnews.feature.auth.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avanade.devnews.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(
                isLoading = false,
                isLoginSuccess = false,
                errorMessage = "Email e senha são obrigatórios."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            val result = loginUseCase(email, password)

            if (result.isSuccess) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    isLoginSuccess = true,
                    errorMessage = null
                )
            } else {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    isLoginSuccess = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Erro ao fazer login."
                )
            }
        }
    }

    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccess = false)
    }
}