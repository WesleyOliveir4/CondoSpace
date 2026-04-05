package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository

class GetPublicationsByCondominiumUseCase(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(condominiumId: String): Result<List<PublicationEntity>> {
        return repository.getPublicationsByCondominium(condominiumId)
    }
}
