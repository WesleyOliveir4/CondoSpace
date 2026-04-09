package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository

class CreatePublicationUseCase(
    private val publicationRepository: PublicationRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(publication: PublicationEntity): Result<Unit> {
        return try {
            val uris = publication.imagesSelectList ?: emptyList()
            
            val uploadedImages = if (uris.isNotEmpty() && uris.any { !it.toString().startsWith("http") }) {
                val uploadResult = imageRepository.uploadImages(uris, "publications/${publication.id}")
                if (uploadResult.isSuccess) {
                    uploadResult.getOrDefault(emptyList())
                } else {
                    return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Erro no upload das imagens"))
                }
            } else {
                emptyList()
            }

            val finalPublication = if (uploadedImages.isNotEmpty()) {
                publication.copy(imageUrlList = uploadedImages)
            } else {
                publication
            }

            publicationRepository.createPublication(finalPublication)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
