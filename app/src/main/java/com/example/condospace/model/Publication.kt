package com.example.condospace.model

data class Publication(
    val id: Int,
    val publicationOwner: String,
    val title: String,
    val description: String,
    val detailedDescription: String,
    val publicationType: String,
    val score: Double? = null,
    val reviewsNumber: Int? = null,
    val imageRes: Int
)