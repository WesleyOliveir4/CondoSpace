package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.presentation.ui.enums.ServiceType

class GetPublicationByIdUseCase(
    private val repository: PublicationRepository,
    private val externalServiceRepository: ExternalServiceRepository
) {
    suspend operator fun invoke(id: String, category: String? = null): Result<PublicationEntity> {
        return if (category == ServiceType.EXTERNAL.value) {
            externalServiceRepository.getExternalServiceById(id)
        } else {
            repository.getPublicationById(id)
        }
    }
}
