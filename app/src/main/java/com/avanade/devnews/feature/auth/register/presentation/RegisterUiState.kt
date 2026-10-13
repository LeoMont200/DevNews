package com.avanade.devnews.feature.auth.register.presentation

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)