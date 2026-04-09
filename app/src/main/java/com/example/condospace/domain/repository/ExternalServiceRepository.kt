package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.ExternalServiceEntity

interface ExternalServiceRepository {
    suspend fun getNearbyServices(cep: String): Result<List<ExternalServiceEntity>>
    suspend fun getExternalServiceById(id: String): Result<ExternalServiceEntity>
}
