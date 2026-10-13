package com.avanade.devnews.domain.usecase.auth

import com.avanade.devnews.domain.model.User
import com.avanade.devnews.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Email, senha e confirmação de senha são obrigatórios.")
            )
        }

        if (password != confirmPassword) {
            return Result.failure(
                IllegalArgumentException("As senhas precisam ser iguais.")
            )
        }

        if (password.length < 6) {
            return Result.failure(
                IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.")
            )
        }

        return repository.register(email, password)
    }
}
