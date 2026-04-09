package com.example.condospace.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ViaCepResponse(
    @SerialName("cep") val cep: String? = null,
    @SerialName("logradouro") val street: String? = null,
    @SerialName("bairro") val neighborhood: String? = null,
    @SerialName("localidade") val city: String? = null,
    @SerialName("uf") val state: String? = null,
    @SerialName("erro") val error: Boolean? = null
)
