package com.example.condospace.domain.entity

import android.net.Uri

data class PublicationEntity(
    val id: String,
    val publicationOwnerUuid: String,
    val publicationCondominiumId: String,
    val publicationOwner: String,
    val serviceProvider: String? = null,
    val contact: String? = null,
    val imageUrlList : List<PublicationImageEntity>? = null,
    val imagesSelectList : List<Uri>? = null,
    val title: String,
    val description: String,
    val publicationType: String,
    val price: Double,
    val likes: Int,
    val date: String,
    val coupon: String? = null
)