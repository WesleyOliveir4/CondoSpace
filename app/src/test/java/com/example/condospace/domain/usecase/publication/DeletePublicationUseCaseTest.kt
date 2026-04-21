package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.repository.PublicationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeletePublicationUseCaseTest {

    private lateinit var publicationRepository: PublicationRepository
    private lateinit var deletePublicationImagesUseCase: DeletePublicationImagesUseCase
    private lateinit var useCase: DeletePublicationUseCase

    @Before
    fun setUp() {
        publicationRepository = mockk()
        deletePublicationImagesUseCase = mockk()
        useCase = DeletePublicationUseCase(publicationRepository, deletePublicationImagesUseCase)
    }

    @Test
    fun `invoke should fetch publication and call delete images before deleting record`() = runTest {
        // Arrange
        val pubId = "pub123"
        val image = PublicationImageEntity("url", "imgId")
        val publication = mockk<PublicationEntity>(relaxed = true)
        every { publication.imageUrlList } returns listOf(image)
        
        coEvery { publicationRepository.getPublicationById(pubId) } returns Result.success(publication)
        coEvery { deletePublicationImagesUseCase(pubId, listOf("imgId")) } returns Result.success(Unit)
        coEvery { publicationRepository.deletePublication(pubId) } returns Result.success(Unit)

        // Act
        val result = useCase(pubId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { deletePublicationImagesUseCase(pubId, listOf("imgId")) }
        coVerify { publicationRepository.deletePublication(pubId) }
    }

    @Test
    fun `invoke should return failure when publication fetching fails`() = runTest {
        // Arrange
        val pubId = "pub123"
        coEvery { publicationRepository.getPublicationById(pubId) } returns Result.failure(Exception("Not found"))

        // Act
        val result = useCase(pubId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { deletePublicationImagesUseCase(any(), any()) }
        coVerify(exactly = 0) { publicationRepository.deletePublication(any()) }
    }
}
