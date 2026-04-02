package com.example.condospace.domain.usecase.login

import com.example.condospace.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.signIn(email, password)
    }
}