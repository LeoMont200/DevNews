package com.avanade.devnews.domain.usecase.auth

import com.avanade.devnews.domain.model.User
import com.avanade.devnews.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
        private val repository: AuthRepository
    ) {

    suspend operator fun invoke (
        email: String,
        password: String
    ) : Result<User> {
        return repository.login(email, password)
    }
}