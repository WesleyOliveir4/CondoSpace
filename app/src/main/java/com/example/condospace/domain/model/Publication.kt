package com.example.condospace.domain.model

data class Publication(
    val id: Int,
    val publicationOwner: String,
    val serviceProvider: String? = null,
    val contact: String? = null,
    val imageUrl: String,
    val title: String,
    val description: String,
    val detailedDescription: String,
    val publicationType: String,
    val price: Double,
    val likes: Int,
    val score: Double? = null,
    val reviewsNumber: Int? = null,
    val imageRes: Int, //Remover
    val date: String
)
