package com.example.condospace.domain.usecase.publication

import android.net.Uri
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditPublicationUseCaseTest {

    private lateinit var publicationRepository: PublicationRepository
    private lateinit var imageRepository: ImageRepository
    private lateinit var useCase: EditPublicationUseCase

    @Before
    fun setUp() {
        publicationRepository = mockk()
        imageRepository = mockk()
        useCase = EditPublicationUseCase(publicationRepository, imageRepository)
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `invoke should delete removed images and upload new ones`() = runTest {
        // Arrange
        val imageKeep = PublicationImageEntity("http://url1.com", "id1")
        val imageDelete = PublicationImageEntity("http://url2.com", "id2")
        
        val uriKeep = mockk<Uri>()
        val uriNew = mockk<Uri>()
        
        every { uriKeep.toString() } returns "http://url1.com"
        every { uriNew.toString() } returns "content://media/new"
        
        val publication = mockk<PublicationEntity>(relaxed = true)
        every { publication.imageUrlList } returns listOf(imageKeep, imageDelete)
        every { publication.imagesSelectList } returns listOf(uriKeep, uriNew)
        every { publication.id } returns "pub123"

        coEvery { imageRepository.deleteImages(any()) } returns Result.success(Unit)
        coEvery { imageRepository.uploadImages(any(), any()) } returns Result.success(listOf(PublicationImageEntity("http://url3.com", "id3")))
        coEvery { publicationRepository.updatePublication(any()) } returns Result.success(Unit)

        // Act
        val result = useCase(publication)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { imageRepository.deleteImages(listOf("id2")) }
        coVerify { imageRepository.uploadImages(listOf(uriNew), "publications/pub123") }
        coVerify { publicationRepository.updatePublication(any()) }
    }

    @Test
    fun `invoke should only update publication when no image changes are needed`() = runTest {
        // Arrange
        val image = PublicationImageEntity("http://url1.com", "id1")
        val uri = mockk<Uri>()
        every { uri.toString() } returns "http://url1.com"
        
        val publication = mockk<PublicationEntity>(relaxed = true)
        every { publication.imageUrlList } returns listOf(image)
        every { publication.imagesSelectList } returns listOf(uri)

        coEvery { publicationRepository.updatePublication(any()) } returns Result.success(Unit)

        // Act
        val result = useCase(publication)

        // Assert
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { imageRepository.deleteImages(any()) }
        coVerify(exactly = 0) { imageRepository.uploadImages(any(), any()) }
    }
}
