package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.ExternalServiceEntity
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.presentation.ui.enums.ServiceType

class GetPublicationByIdUseCase(
    private val repository: PublicationRepository,
    private val externalServiceRepository: ExternalServiceRepository
) {
    suspend operator fun invoke(id: String, category: String? = null): Result<PublicationEntity> {
        return if (category == ServiceType.EXTERNAL.value) {
            externalServiceRepository.getExternalServiceById(id).map { it.toPublicationEntity() }
        } else {
            repository.getPublicationById(id)
        }
    }

    private fun ExternalServiceEntity.toPublicationEntity(): PublicationEntity {
        return PublicationEntity(
            id = this.id,
            publicationOwnerUuid = "",
            publicationCondominiumId = "",
            publicationOwner = this.publicationOwner,
            title = this.title,
            description = this.description,
            publicationType = this.publicationType,
            price = this.price,
            likes = 0,
            date = this.date,
            imageUrlList = this.imageUrlList?.map { 
                PublicationImageEntity(url = it.url, publicId = it.publicId) 
            },
            coupon = this.coupon
        )
    }
}
