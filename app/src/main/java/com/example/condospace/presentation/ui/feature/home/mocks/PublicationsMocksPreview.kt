package com.example.condospace.presentation.ui.feature.home.mocks

import com.example.condospace.presentation.model.PublicationImageUiModel
import com.example.condospace.presentation.model.PublicationUiModel

class PublicationsMocksPreview {

    val listMockUi  = listOf(
        PublicationUiModel(
            id = "1",
            publicationOwnerUuid = "uuid",
            publicationCondominiumId = "condoId",
            publicationOwner = "João Silva",
            title = "Pintura Residencial",
            description = "Ofereço serviços de pintura interna e externa com ótimo acabamento e preço justo.",
            publicationType = "Serviço",
            price = 150.0,
            likes = 42,
            date = "2023-10-27",
            imageUrlList = listOf(PublicationImageUiModel(url = "https://example.com/image.jpg", publicId = "1"))
        ),
        PublicationUiModel(
            id = "2",
            publicationOwnerUuid = "uuid",
            publicationCondominiumId = "condoId",
            publicationOwner = "João Silva",
            title = "Pintura Residencial",
            description = "Ofereço serviços de pintura interna e externa com ótimo acabamento e preço justo.",
            publicationType = "Serviço",
            price = 150.0,
            likes = 42,
            date = "2023-10-27",
            imageUrlList = listOf(PublicationImageUiModel(url = "https://example.com/image.jpg", publicId = "1"))
        ),
        PublicationUiModel(
            id = "3",
            publicationOwnerUuid = "uuid",
            publicationCondominiumId = "condoId",
            publicationOwner = "João Silva",
            title = "Pintura Residencial",
            description = "Ofereço serviços de pintura interna e externa com ótimo acabamento e preço justo.",
            publicationType = "Serviço",
            price = 150.0,
            likes = 42,
            date = "2023-10-27",
            imageUrlList = listOf(PublicationImageUiModel(url = "https://example.com/image.jpg", publicId = "1"))
        )
    )
}