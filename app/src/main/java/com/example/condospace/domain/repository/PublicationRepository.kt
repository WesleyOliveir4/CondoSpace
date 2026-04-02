package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.PublicationEntity

interface PublicationRepository {
    suspend fun createPublication(publication: PublicationEntity): Result<Unit>
    suspend fun getPublicationsByCondominium(condominiumName: String): Result<List<PublicationEntity>>
    suspend fun getPublicationsByUser(userId: String): Result<List<PublicationEntity>>
}
