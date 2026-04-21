package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.repository.ImageRepository
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
class DeletePublicationImagesUseCaseTest {

    private lateinit var imageRepository: ImageRepository
    private lateinit var useCase: DeletePublicationImagesUseCase

    @Before
    fun setUp() {
        imageRepository = mockk()
        useCase = DeletePublicationImagesUseCase(imageRepository)
    }

    @Test
    fun `invoke should delete images and folder when publicIds is not empty`() = runTest {
        // Arrange
        val publicationId = "pub123"
        val publicIds = listOf("id1", "id2")
        coEvery { imageRepository.deleteImages(publicIds) } returns Result.success(Unit)
        coEvery { imageRepository.deleteFolder(any()) } returns Result.success(Unit)

        // Act
        val result = useCase(publicationId, publicIds)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { imageRepository.deleteImages(publicIds) }
        coVerify { imageRepository.deleteFolder("publications/$publicationId") }
    }

    @Test
    fun `invoke should not call repository when publicIds is empty`() = runTest {
        // Arrange
        val publicationId = "pub123"
        val publicIds = emptyList<String>()

        // Act
        val result = useCase(publicationId, publicIds)

        // Assert
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { imageRepository.deleteImages(any()) }
        coVerify(exactly = 0) { imageRepository.deleteFolder(any()) }
    }

    @Test
    fun `invoke should return failure when deleteImages fails`() = runTest {
        // Arrange
        val publicationId = "pub123"
        val publicIds = listOf("id1")
        val exception = Exception("Delete failed")
        coEvery { imageRepository.deleteImages(any()) } returns Result.failure(exception)

        // Act
        val result = useCase(publicationId, publicIds)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { imageRepository.deleteFolder(any()) }
    }
}
