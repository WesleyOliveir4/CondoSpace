package com.example.condospace.domain.usecase.register

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userEntity: UserEntity): Result<Unit> {
        return userRepository.createUser(userEntity)
    }
}