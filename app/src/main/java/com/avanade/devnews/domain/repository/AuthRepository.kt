package com.avanade.devnews.domain.repository

import com.avanade.devnews.domain.model.User

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    fun getCurrentUser(): User?

    fun logout()

    suspend fun register(
        email: String,
        password: String
    ): Result<User>
}