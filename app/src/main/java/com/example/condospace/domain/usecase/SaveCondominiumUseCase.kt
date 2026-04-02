package com.example.condospace.domain.usecase

import com.example.condospace.domain.model.Condominium
import com.example.condospace.domain.repository.CondominiumRepository
import com.example.condospace.domain.repository.UserRepository

class SaveCondominiumUseCase(
    private val condominiumRepository: CondominiumRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, condominium: Condominium): Result<Unit> {
        val saveCondoResult = condominiumRepository.saveCondominium(condominium)
        if (saveCondoResult.isFailure) return saveCondoResult
        
        return userRepository.updateUserCondominium(
            userId = userId,
            condominiumName = condominium.name,
            cep = condominium.cep
        )
    }
}
