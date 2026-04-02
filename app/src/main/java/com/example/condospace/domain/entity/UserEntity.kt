package com.example.condospace.domain.entity

data class UserEntity(
    val uuid: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String?,
    val email: String,
    val condominiumEntity: CondominiumEntity? = null,
    val publicationsIdFavored: List<String>? = null
)