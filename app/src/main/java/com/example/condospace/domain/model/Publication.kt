package com.example.condospace.domain.model

import android.net.Uri


data class Publication(
    val id: String,
    val publicationOwnerUuid: String,
    val publicationCondominiumId: String,
    val publicationOwner: String,
    val serviceProvider: String? = null,
    val contact: String? = null,
    val imageUrlList : List<PublicationImage>? = null,
    val imagesSelectList : List<Uri>? = null,
    val title: String,
    val description: String,
    val publicationType: String,
    val price: Double,
    val likes: Int,
    val date: String
)
