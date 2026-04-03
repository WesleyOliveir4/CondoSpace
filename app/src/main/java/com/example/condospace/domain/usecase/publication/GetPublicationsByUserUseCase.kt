package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository

class GetPublicationsByUserUseCase(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(userId: String): Result<List<PublicationEntity>> {
        return repository.getPublicationsByUser(userId)
    }
}
