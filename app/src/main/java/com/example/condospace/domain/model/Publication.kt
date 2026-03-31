package com.example.condospace.domain.model

import android.net.Uri


data class Publication(
    val id: Int,
    val publicationOwnerUuid: String,
    val publicationOwner: String,
    val serviceProvider: String? = null,
    val contact: String? = null,
    val imageUrlList : List<String>? = null,
    val imagesSelectList : List<Uri>? = null,
    val title: String,
    val description: String,
    val publicationType: String,
    val price: Double,
    val likes: Int,
    val date: String
)
