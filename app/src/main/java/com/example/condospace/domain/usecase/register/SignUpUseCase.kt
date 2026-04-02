package com.example.condospace.domain.usecase.register

import com.example.condospace.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return authRepository.signUp(email, password)
    }
}