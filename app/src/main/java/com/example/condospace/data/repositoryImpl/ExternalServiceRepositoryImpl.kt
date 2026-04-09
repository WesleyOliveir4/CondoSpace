package com.example.condospace.data.repositoryImpl

import com.example.condospace.data.model.PublicationImage
import com.example.condospace.data.remote.OpenCageService
import com.example.condospace.data.remote.ViaCepService
import com.example.condospace.domain.entity.ExternalServiceEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class ExternalServiceRepositoryImpl(
    private val viaCepService: ViaCepService,
    private val openCageService: OpenCageService,
    private val firestore: FirebaseFirestore
) : ExternalServiceRepository {

    override suspend fun getNearbyServices(cep: String): Result<List<ExternalServiceEntity>> {
        return runCatching {
            // 1. ViaCep
            val viaCepResponse = viaCepService.getAddressByCep(cep)
            if (viaCepResponse.error == true) throw Exception("CEP não encontrado")

            val street = viaCepResponse.street
            val city = viaCepResponse.city
            val neighborhood = viaCepResponse.neighborhood

            // 2. OpenCage Geocoding
            val query = if (!street.isNullOrBlank()) {
                "$street, $city, Brazil"
            } else {
                "$neighborhood, $city, Brazil"
            }.replace(" ", "+")

            val openCageResponse = openCageService.getGeocoding(query)
            if (openCageResponse.status.code != 200) throw Exception(openCageResponse.status.message)

            // 3. Find best result
            val bestResult = openCageResponse.results.find { 
                it.components.postcode?.replace("-", "") == cep.replace("-", "") 
            } ?: openCageResponse.results.maxByOrNull { it.confidence }
            ?: throw Exception("Localização não encontrada")

            val targetLat = bestResult.geometry.lat
            val targetLng = bestResult.geometry.lng

            // 4. Firestore query
            val snapshot = firestore.collection("services").get().await()
            val nearbyServices = mutableListOf<ExternalServiceEntity>()

            for (doc in snapshot.documents) {
                val key = doc.id // e.g., "-23.6850989,-46.6816044"
                val coords = key.split(",")
                if (coords.size == 2) {
                    val lat = coords[0].trim().toDoubleOrNull()
                    val lng = coords[1].trim().toDoubleOrNull()

                    if (lat != null && lng != null) {
                        val distance = calculateDistance(targetLat, targetLng, lat, lng)
                        if (distance <= 3.0) {
                            val data = doc.data
                            if (data != null) {
                                // Tratamento seguro para imageUrlList que pode vir como Map ou List
                                val imagesRaw = data["imageUrlList"]
                                val imagesList = when (imagesRaw) {
                                    is List<*> -> {
                                        imagesRaw.filterIsInstance<Map<String, Any>>().map {
                                            PublicationImage(
                                                url = it["url"] as? String ?: "",
                                                publicId = it["publicId"] as? String ?: ""
                                            )
                                        }
                                    }
                                    is Map<*, *> -> {
                                        imagesRaw.values.filterIsInstance<Map<String, Any>>().map {
                                            PublicationImage(
                                                url = it["url"] as? String ?: "",
                                                publicId = it["publicId"] as? String ?: ""
                                            )
                                        }
                                    }
                                    else -> null
                                }

                                nearbyServices.add(
                                    ExternalServiceEntity(
                                        id = doc.id,
                                        publicationOwner = data["publicationOwner"] as? String ?: "",
                                        publicationType = data["publicationType"] as? String ?: "",
                                        title = data["title"] as? String ?: "",
                                        date = data["date"] as? String ?: "",
                                        coupon = data["coupon"] as? String ?: "",
                                        description = data["description"] as? String ?: "",
                                        imageUrlList = imagesList,
                                        price = (data["price"] as? Number)?.toDouble() ?: 0.0
                                    )
                                )
                            }
                        }
                    }
                }
            }
            nearbyServices
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
