package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.UserRepository

class UpdateUserCondominiumUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, condominiumEntity: CondominiumEntity): Result<Unit> {
        return userRepository.updateUserCondominium(userId = userId, condominium = condominiumEntity)
    }
}
