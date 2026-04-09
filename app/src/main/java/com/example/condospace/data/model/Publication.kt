package com.example.condospace.data.model

import android.net.Uri
import com.google.firebase.firestore.Exclude


data class Publication(
    val id: String = "",
    val publicationOwnerUuid: String = "",
    val publicationCondominiumId: String = "",
    val publicationOwner: String = "",
    val serviceProvider: String? = null,
    val contact: String? = null,
    val imageUrlList : List<PublicationImage>? = null,
    @get:Exclude val imagesSelectList : List<Uri>? = null,
    val title: String = "",
    val description: String = "",
    val publicationType: String = "",
    val price: Double = 0.0,
    val likes: Int = 0,
    val date: String = "",
    val coupon: String? = null
)
