package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.repository.PublicationRepository

class DeletePublicationUseCase(
    private val publicationRepository: PublicationRepository,
    private val deletePublicationImagesUseCase: DeletePublicationImagesUseCase
) {
    suspend operator fun invoke(publicationId: String): Result<Unit> {
        return try {
            val getResult = publicationRepository.getPublicationById(publicationId)
            
            getResult.onSuccess { entity ->
                val publicIds = entity.imageUrlList?.map { it.publicId } ?: emptyList()
                
                deletePublicationImagesUseCase(publicationId, publicIds)

                return publicationRepository.deletePublication(publicationId)
            }.onFailure { e ->
                return Result.failure(Exception("Erro ao buscar publicação para exclusão: ${e.message}"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
