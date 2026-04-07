package com.example.condospace.domain.usecase.user

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<UserEntity?> {
        return userRepository.getUser(userId)
    }
}
