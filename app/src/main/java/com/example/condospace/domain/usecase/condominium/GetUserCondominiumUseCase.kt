package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.UserRepository

class GetUserCondominiumUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<CondominiumEntity?> {
        return userRepository.getUser(userId).map { userEntity ->
            if (userEntity != null && !userEntity.condominiumEntity?.cep.isNullOrBlank() && userEntity.condominiumEntity.name.isNotBlank()) {
                CondominiumEntity(name = userEntity.condominiumEntity.name, cep = userEntity.condominiumEntity.cep, id = userEntity.condominiumEntity.id)
            } else {
                null
            }
        }
    }
}
