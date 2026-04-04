package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow

class GetPublicationsByUserUseCase(
    private val repository: PublicationRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<PublicationEntity>>> {
        return repository.getPublicationsByUser(userId)
    }
}
