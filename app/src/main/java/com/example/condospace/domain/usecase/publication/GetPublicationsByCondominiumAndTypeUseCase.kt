package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository

class GetPublicationsByCondominiumAndTypeUseCase(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(condominiumId: String, type: String?): Result<List<PublicationEntity>> {
        return repository.getPublicationsByCondominiumAndType(condominiumId, type)
    }
}
