package com.example.condospace.domain.usecase.publication

import android.net.Uri
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreatePublicationUseCaseTest {

    private lateinit var publicationRepository: PublicationRepository
    private lateinit var imageRepository: ImageRepository
    private lateinit var useCase: CreatePublicationUseCase

    @Before
    fun setUp() {
        publicationRepository = mockk()
        imageRepository = mockk()
        useCase = CreatePublicationUseCase(publicationRepository, imageRepository)
    }

    @Test
    fun `invoke should return success when images upload and publication creation succeed`() = runTest {
        // Arrange
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://media/external/images/1"
        val publication = mockk<PublicationEntity>(relaxed = true)
        every { publication.imagesSelectList } returns listOf(uri)
        every { publication.id } returns "pub123"
        
        val uploadedImages = listOf(PublicationImageEntity("url", "id"))
        
        coEvery { imageRepository.uploadImages(any(), any()) } returns Result.success(uploadedImages)
        coEvery { publicationRepository.createPublication(any()) } returns Result.success(Unit)

        // Act
        val result = useCase(publication)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when image upload fails`() = runTest {
        // Arrange
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://media/external/images/1"
        val publication = mockk<PublicationEntity>(relaxed = true)
        every { publication.imagesSelectList } returns listOf(uri)
        every { publication.id } returns "pub123"
        
        val exception = Exception("Upload error")
        coEvery { imageRepository.uploadImages(any(), any()) } returns Result.failure(exception)

        // Act
        val result = useCase(publication)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should return success when no images to upload`() = runTest {
        // Arrange
        val publication = mockk<PublicationEntity>(relaxed = true)
        every { publication.imagesSelectList } returns emptyList()
        coEvery { publicationRepository.createPublication(any()) } returns Result.success(Unit)

        // Act
        val result = useCase(publication)

        // Assert
        assertTrue(result.isSuccess)
    }
}
