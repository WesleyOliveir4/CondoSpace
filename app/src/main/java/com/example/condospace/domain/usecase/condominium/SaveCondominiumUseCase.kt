package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.CondominiumRepository
import com.example.condospace.domain.repository.UserRepository

class SaveCondominiumUseCase(
    private val condominiumRepository: CondominiumRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, condominiumEntity: CondominiumEntity): Result<Unit> {
        val saveCondoResult = condominiumRepository.saveCondominium(condominiumEntity)
        if (saveCondoResult.isFailure) return saveCondoResult
        
        return userRepository.updateUserCondominium(
            userId = userId,
            condominium = condominiumEntity
        )
    }
}
