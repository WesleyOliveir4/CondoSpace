package com.example.condospace.presentation.model

import com.example.condospace.domain.entity.PublicationImageEntity

data class PublicationImageUiModel(
    val url: String,
    val publicId: String
)

fun PublicationImageEntity.toUiModel(): PublicationImageUiModel{
    return PublicationImageUiModel(
        url = url,
        publicId = publicId
    )
}

fun PublicationImageUiModel.toEntity(): PublicationImageEntity{
    return PublicationImageEntity(
        url = url,
        publicId = publicId
    )
}