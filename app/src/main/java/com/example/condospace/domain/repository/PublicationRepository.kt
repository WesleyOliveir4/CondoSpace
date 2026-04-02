package com.example.condospace.domain.repository

import com.example.condospace.domain.model.Publication

interface PublicationRepository {
    suspend fun createPublication(publication: Publication): Result<Unit>
    suspend fun getPublicationsByCondominium(condominiumName: String): Result<List<Publication>>
    suspend fun getPublicationsByUser(userId: String): Result<List<Publication>>
}
