package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPublicationsByCondominiumUseCaseTest {

    private lateinit var publicationRepository: PublicationRepository
    private lateinit var useCase: GetPublicationsByCondominiumUseCase

    @Before
    fun setUp() {
        publicationRepository = mockk()
        useCase = GetPublicationsByCondominiumUseCase(publicationRepository)
    }

    @Test
    fun `invoke should return publications from repository`() = runTest {
        // Arrange
        val condoId = "condo123"
        val publications = listOf(mockk<PublicationEntity>(), mockk<PublicationEntity>())
        coEvery { publicationRepository.getPublicationsByCondominium(condoId) } returns Result.success(publications)

        // Act
        val result = useCase(condoId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Arrange
        val condoId = "condo123"
        val exception = Exception("Error")
        coEvery { publicationRepository.getPublicationsByCondominium(condoId) } returns Result.failure(exception)

        // Act
        val result = useCase(condoId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
