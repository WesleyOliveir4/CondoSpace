package com.example.condospace.domain.usecase

import com.example.condospace.domain.repository.UserRepository

class UpdateUserCondominiumUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, condominiumName: String, cep: String): Result<Unit> {
        return userRepository.updateUserCondominium(userId, condominiumName, cep)
    }
}
