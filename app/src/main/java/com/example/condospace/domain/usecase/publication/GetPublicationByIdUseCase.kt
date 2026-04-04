package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository

class GetPublicationByIdUseCase(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(id: String): Result<PublicationEntity> {
        return repository.getPublicationById(id)
    }
}
