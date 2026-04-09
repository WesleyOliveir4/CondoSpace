package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository

class EditPublicationUseCase(
    private val publicationRepository: PublicationRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(publication: PublicationEntity): Result<Unit> {
        return try {
            val selectedUris = publication.imagesSelectList ?: emptyList()
            val existingImages = publication.imageUrlList ?: emptyList()

            // Filtra as imagens que devem ser mantidas (estão na lista selecionada e já possuem URL do Cloudinary)
            // Usamos a URL para comparar se a imagem existente ainda está entre as selecionadas.
            val imagesToKeep = existingImages.filter { img ->
                selectedUris.any { it.toString() == img.url }
            }

            // Identifica os publicIds das imagens que foram REMOVIDAS pelo usuário (estavam no Cloudinary mas não estão mais na lista)
            val publicIdsToDelete = existingImages
                .filter { img -> imagesToKeep.none { it.publicId == img.publicId } }
                .map { it.publicId }

            if (publicIdsToDelete.isNotEmpty()) {
                imageRepository.deleteImages(publicIdsToDelete)
            }

            // Identifica as NOVAS imagens que precisam de upload (não são URLs http/https do Cloudinary)
            val urisToUpload = selectedUris.filter { !it.toString().startsWith("http") }

            val uploadedImages = if (urisToUpload.isNotEmpty()) {
                val uploadResult = imageRepository.uploadImages(urisToUpload, "publications/${publication.id}")
                if (uploadResult.isSuccess) {
                    uploadResult.getOrDefault(emptyList())
                } else {
                    return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Erro no upload das imagens"))
                }
            } else {
                emptyList()
            }

            // Junta as imagens mantidas (que já estavam lá) com as novas imagens (recém enviadas)
            val finalImages = imagesToKeep + uploadedImages

            val finalPublication = publication.copy(
                imageUrlList = finalImages,
                imagesSelectList = null // Limpa a lista de seleção para salvar apenas as URLs finais
            )

            publicationRepository.updatePublication(finalPublication)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
