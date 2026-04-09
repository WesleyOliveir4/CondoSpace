package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.PublicationEntity

interface ExternalServiceRepository {
    suspend fun getNearbyServices(cep: String): Result<List<PublicationEntity>>
    suspend fun getExternalServiceById(id: String): Result<PublicationEntity>
    suspend fun getExternalServicesByIds(ids: List<String>): Result<List<PublicationEntity>>
}
