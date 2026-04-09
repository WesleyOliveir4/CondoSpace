package com.example.condospace.presentation.model

import android.net.Uri
import com.example.condospace.domain.entity.PublicationEntity

data class PublicationUiModel(
    val id: String,
    val publicationOwnerUuid: String = "",
    val publicationCondominiumId: String = "",
    val publicationOwner: String,
    val serviceProvider: String? = null,
    val contact: String? = null,
    val imageUrlList : List<PublicationImageUiModel>? = null,
    val imagesSelectList : List<Uri>? = null,
    val title: String,
    val description: String,
    val publicationType: String,
    val price: Double,
    val likes: Int = 0,
    val date: String,
    val coupon: String? = null,
)


fun PublicationEntity.toUiModel(): PublicationUiModel{
    return PublicationUiModel(
        id = id,
        publicationOwnerUuid = publicationOwnerUuid,
        publicationCondominiumId = publicationCondominiumId,
        publicationOwner = publicationOwner,
        serviceProvider = serviceProvider,
        contact = contact,
        imageUrlList = imageUrlList?.map { it.toUiModel() },
        imagesSelectList = imagesSelectList,
        title = title,
        description = description,
        publicationType = publicationType,
        price = price,
        likes = likes,
        date = date,
    )
}

fun PublicationUiModel.toEntity(): PublicationEntity{
    return PublicationEntity(
        id = id,
        publicationOwnerUuid = publicationOwnerUuid,
        publicationCondominiumId = publicationCondominiumId,
        publicationOwner = publicationOwner,
        serviceProvider = serviceProvider,
        contact = contact,
        imageUrlList = imageUrlList?.map { it.toEntity() },
        imagesSelectList = imagesSelectList,
        title = title,
        description = description,
        publicationType = publicationType,
        price = price,
        likes = likes,
        date = date
    )
}
