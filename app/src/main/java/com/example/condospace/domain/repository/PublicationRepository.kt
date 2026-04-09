package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.PublicationEntity
import kotlinx.coroutines.flow.Flow

interface PublicationRepository {
    suspend fun createPublication(publication: PublicationEntity): Result<Unit>
    suspend fun getPublicationsByCondominium(condominiumId: String): Result<List<PublicationEntity>>
    suspend fun getPublicationsByCondominiumAndType(condominiumId: String, type: String?): Result<List<PublicationEntity>>
    fun getPublicationsByUser(userId: String): Flow<Result<List<PublicationEntity>>>
    suspend fun getPublicationById(id: String): Result<PublicationEntity>
    suspend fun deletePublication(publicationId: String): Result<Unit>
    suspend fun updatePublicationLikes(publicationId: String, increment: Int): Result<Unit>
    suspend fun updatePublication(publication: PublicationEntity): Result<Unit>
    suspend fun getPublicationsByIds(ids: List<String>): Result<List<PublicationEntity>>
}
