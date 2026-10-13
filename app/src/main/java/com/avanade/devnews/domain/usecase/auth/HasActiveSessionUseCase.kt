package com.avanade.devnews.domain.usecase.auth

import com.avanade.devnews.domain.repository.AuthRepository
import javax.inject.Inject

class HasActiveSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return repository.hasActiveSession()
    }
}
