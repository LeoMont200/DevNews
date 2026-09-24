package com.avanade.devnews.feature.auth.session.presentation

import androidx.lifecycle.ViewModel
import com.avanade.devnews.domain.usecase.auth.HasActiveSessionUseCase
import com.avanade.devnews.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionUiState(
    val isCheckingSession: Boolean = true,
    val isSessionActive: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val hasActiveSessionUseCase: HasActiveSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        refreshSessionState()
    }

    fun refreshSessionState() {
        _uiState.value = SessionUiState(
            isCheckingSession = false,
            isSessionActive = hasActiveSessionUseCase()
        )
    }

    fun logout() {
        logoutUseCase()
        _uiState.value = SessionUiState(
            isCheckingSession = false,
            isSessionActive = false
        )
    }
}
