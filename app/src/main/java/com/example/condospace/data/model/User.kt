package com.example.condospace.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User (
    val uuid: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profilePicture: String? = null,
    val email: String = "",
    val condominium: Condominium? = null,
    val publicationsIdFavored: List<String>? = null,
    val notificationsEnabled: Boolean = true
)