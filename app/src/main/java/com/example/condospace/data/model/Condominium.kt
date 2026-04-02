package com.example.condospace.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Condominium(
    var id: String? = null,
    val name: String = "",
    val cep: String = "",
)