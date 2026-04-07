package com.example.condospace.domain.usecase.user

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(user: UserEntity): Result<Unit> {
        val firebaseResult = userRepository.updateUser(user)
        if (firebaseResult.isSuccess) {
            userPreferencesRepository.saveUserData(user)
        }
        return firebaseResult
    }
}
