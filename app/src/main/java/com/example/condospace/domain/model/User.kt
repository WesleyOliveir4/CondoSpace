package com.example.condospace.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val uuid: String,
    val name: String,
    val phoneNumber: String,
    val profilePicture: String,
    val email: String,
    val cep: String,
    val condominiumName: String
)