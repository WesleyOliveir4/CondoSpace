package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository

class UpdateUserCondominiumUseCase(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(userId: String, condominiumEntity: CondominiumEntity): Result<Unit> {
        return userRepository.updateUserCondominium(userId = userId, condominium = condominiumEntity)
            .onSuccess {
                try {
                    val currentUser = userPreferencesRepository.getUserData()
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(condominiumEntity = condominiumEntity)
                        userPreferencesRepository.saveUserData(updatedUser)
                    }
                } catch (e: Exception) {
                    // Tratar futuramente enviando para alguma ferramenta de observabilidade
                }
            }
    }
}
