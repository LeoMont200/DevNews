package com.avanade.devnews.domain.usecase.auth

import com.avanade.devnews.domain.model.User
import com.avanade.devnews.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
        private val repository: AuthRepository
    ) {

    suspend operator fun invoke (
        email: String,
        password: String,
        rememberMe: Boolean
    ) : Result<User> {
        val result = repository.login(email, password)

        if (result.isSuccess) {
            repository.setRememberMe(rememberMe)
        }

        return result
    }
}