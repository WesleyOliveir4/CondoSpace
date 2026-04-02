package com.example.condospace.domain.usecase

import com.example.condospace.domain.model.Condominium
import com.example.condospace.domain.repository.UserRepository

class GetUserCondominiumUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Condominium?> {
        return userRepository.getUser(userId).map { user ->
            if (user != null && !user.cep.isNullOrBlank() && !user.condominiumName.isNullOrBlank()) {
                Condominium(name = user.condominiumName, cep = user.cep)
            } else {
                null
            }
        }
    }
}
