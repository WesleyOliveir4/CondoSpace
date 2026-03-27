package com.example.condospace.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val phoneNumber: String,
    val profilePicture: Int,
    val email: String,
    val cep: String,
    val condominiumName: String
)