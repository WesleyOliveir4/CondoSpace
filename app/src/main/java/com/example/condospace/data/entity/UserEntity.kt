package com.example.condospace.data.entity

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class UserEntity(
    val uuid: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profilePicture: String? = null,
    val email: String = "",
    val cep: String? = null,
    val condominiumName: String? = null
)