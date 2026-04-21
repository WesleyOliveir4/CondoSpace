package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoritePublicationsUseCaseTest {

    private lateinit var publicationRepository: PublicationRepository
    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var useCase: GetFavoritePublicationsUseCase

    @Before
    fun setUp() {
        publicationRepository = mockk()
        userRepository = mockk()
        userPreferencesRepository = mockk()
        useCase = GetFavoritePublicationsUseCase(
            publicationRepository,
            userRepository,
            userPreferencesRepository
        )
    }

    @Test
    fun `invoke should return valid publications and clean up invalid ones`() = runTest {
        // Arrange
        val userId = "user123"
        val favoriteIds = listOf("valid1", "invalid1")
        val validPub = mockk<PublicationEntity>()
        val userEntity = UserEntity(
            uuid = userId,
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            publicationsIdFavored = favoriteIds
        )

        coEvery { publicationRepository.getPublicationById("valid1") } returns Result.success(validPub)
        coEvery { publicationRepository.getPublicationById("invalid1") } returns Result.failure(Exception("Not found"))
        
        coEvery { userRepository.updateFavoritePublications(userId, listOf("valid1")) } returns Result.success(Unit)
        coEvery { userPreferencesRepository.getUserData() } returns userEntity
        coEvery { userPreferencesRepository.saveUserData(any()) } returns Unit

        // Act
        val result = useCase(userId, favoriteIds)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify { userRepository.updateFavoritePublications(userId, listOf("valid1")) }
        coVerify { userPreferencesRepository.saveUserData(match { it.publicationsIdFavored?.size == 1 }) }
    }

    @Test
    fun `invoke should return all publications when all ids are valid`() = runTest {
        // Arrange
        val userId = "user123"
        val favoriteIds = listOf("id1", "id2")
        val pub = mockk<PublicationEntity>()

        coEvery { publicationRepository.getPublicationById(any()) } returns Result.success(pub)

        // Act
        val result = useCase(userId, favoriteIds)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        coVerify(exactly = 0) { userRepository.updateFavoritePublications(any(), any()) }
    }
}
