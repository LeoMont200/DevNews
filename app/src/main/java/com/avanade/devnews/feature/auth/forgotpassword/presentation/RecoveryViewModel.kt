package com.avanade.devnews.feature.auth.forgotpassword.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avanade.devnews.domain.usecase.auth.RecoverPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecoveryUiState(
    val isLoading: Boolean = false,
    val isRecoverySuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class RecoveryViewModel @Inject constructor(
    private val recoverPasswordUseCase: RecoverPasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    fun recoverPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = RecoveryUiState(isLoading = true)

            val result = recoverPasswordUseCase(email)

            if (result.isSuccess) {
                _uiState.value = RecoveryUiState(
                    isLoading = false,
                    isRecoverySuccess = true,
                    successMessage = "Email de redefinicao enviado com sucesso."
                )
            } else {
                _uiState.value = RecoveryUiState(
                    isLoading = false,
                    isRecoverySuccess = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Erro ao enviar email de redefinicao."
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            isRecoverySuccess = false
        )
    }
}
