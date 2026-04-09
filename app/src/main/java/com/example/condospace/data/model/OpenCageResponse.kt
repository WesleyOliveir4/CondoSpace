package com.example.condospace.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenCageResponse(
    val results: List<OpenCageResult>,
    val status: OpenCageStatus
)

@Serializable
data class OpenCageResult(
    val components: OpenCageComponents,
    val confidence: Int,
    val geometry: GeometryDto
)

@Serializable
data class OpenCageComponents(
    @SerialName("postcode") val postcode: String? = null
)

@Serializable
data class GeometryDto(
    val lat: Double,
    val lng: Double
)

@Serializable
data class OpenCageStatus(
    val code: Int,
    val message: String
)
