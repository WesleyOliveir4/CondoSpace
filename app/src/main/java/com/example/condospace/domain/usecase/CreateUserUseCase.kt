package com.example.condospace.domain.usecase

import com.example.condospace.domain.model.User
import com.example.condospace.domain.repository.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.createUser(user)
    }
}
