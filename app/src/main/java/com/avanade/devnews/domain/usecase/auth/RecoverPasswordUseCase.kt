package com.avanade.devnews.domain.usecase.auth

import android.util.Patterns
import com.avanade.devnews.domain.repository.AuthRepository
import javax.inject.Inject

class RecoverPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email e obrigatorio."))
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("Informe um email valido."))
        }

        return repository.recoverPassword(email)
    }
}