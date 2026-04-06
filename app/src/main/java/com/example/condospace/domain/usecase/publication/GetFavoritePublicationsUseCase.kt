package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.repository.UserPreferencesRepository

class GetFavoritePublicationsUseCase(
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(userId: String, favoriteIds: List<String>): Result<List<PublicationEntity>> {
        val validPublications = mutableListOf<PublicationEntity>()
        val invalidIds = mutableListOf<String>()

        favoriteIds.forEach { id ->
            publicationRepository.getPublicationById(id).fold(
                onSuccess = { validPublications.add(it) },
                onFailure = { 
                    invalidIds.add(id)
                }
            )
        }

        if (invalidIds.isNotEmpty()) {
            val updatedFavorites = favoriteIds.filterNot { it in invalidIds }
            
            userRepository.updateFavoritePublications(userId, updatedFavorites)
            
            val currentUser = userPreferencesRepository.getUserData()
            currentUser?.let {
                userPreferencesRepository.saveUserData(it.copy(publicationsIdFavored = updatedFavorites))
            }
        }

        return Result.success(validPublications)
    }
}
