package com.example.condospace.domain.entity

import com.example.condospace.data.model.PublicationImage

data class ExternalServiceEntity(
    val id: String,
    val publicationOwner: String,
    val publicationType: String,
    val title: String,
    val date: String,
    val coupon: String,
    val description: String,
    val imageUrlList: List<PublicationImage>? = null,
    val price: Double
)
