package com.example.condospace.presentation.model

import com.example.condospace.domain.entity.ExternalServiceEntity

data class ExternalServiceUiModel(
    val id: String,
    val publicationOwner: String,
    val publicationType: String,
    val title: String,
    val date: String,
    val coupon: String,
    val description: String,
    val imageUrlList: List<PublicationImageUiModel>? = null,
    val price: Double
)

fun ExternalServiceEntity.toUiModel(): ExternalServiceUiModel {
    return ExternalServiceUiModel(
        id = id,
        publicationOwner = publicationOwner,
        publicationType = publicationType,
        title = title,
        date = date,
        coupon = coupon,
        description = description,
        imageUrlList = imageUrlList?.map { PublicationImageUiModel(it.url, it.publicId) },
        price = price
    )
}

fun ExternalServiceUiModel.toPublicationUiModel(): PublicationUiModel {
    return PublicationUiModel(
        id = id,
        publicationOwnerUuid = "",
        publicationCondominiumId = "",
        publicationOwner = publicationOwner,
        title = title,
        description = description,
        publicationType = publicationType,
        price = price,
        likes = 0,
        date = date,
        imageUrlList = imageUrlList,
        isExternal = true
    )
}
