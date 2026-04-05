package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.repository.ImageRepository

class DeletePublicationImagesUseCase(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(publicationId: String, publicIds: List<String>): Result<Unit> {
        return try {
            if (publicIds.isNotEmpty()) {
                val deleteImagesResult = imageRepository.deleteImages(publicIds)
                if (deleteImagesResult.isFailure) return deleteImagesResult
                val folderPath = "publications/$publicationId"
                imageRepository.deleteFolder(folderPath)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
