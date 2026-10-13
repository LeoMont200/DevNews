package com.avanade.devnews.feature.auth.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avanade.devnews.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)

            val result = registerUseCase(email, password, confirmPassword)

            _uiState.value = if (result.isSuccess) {
                RegisterUiState(isSuccess = true)
            } else {
                RegisterUiState(
                    error = result.exceptionOrNull()?.message ?: "Erro ao cadastrar usuário."
                )
            }
        }
    }

    fun resetRegisterSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
