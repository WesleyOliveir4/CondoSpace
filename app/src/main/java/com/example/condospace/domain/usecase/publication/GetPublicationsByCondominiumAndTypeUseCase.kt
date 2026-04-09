package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.presentation.ui.enums.ServiceType

class GetPublicationsByCondominiumAndTypeUseCase(
    private val repository: PublicationRepository,
    private val externalServiceRepository: ExternalServiceRepository
) {
    suspend operator fun invoke(category: String?, publicationsIds: List<String>): Result<List<PublicationEntity>> {
        return if (category == ServiceType.EXTERNAL.value) {
            externalServiceRepository.getExternalServicesByIds(publicationsIds)
        } else {
            repository.getPublicationsByIds(publicationsIds)
        }
    }
}
