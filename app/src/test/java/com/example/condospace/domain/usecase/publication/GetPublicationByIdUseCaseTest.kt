package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.presentation.ui.enums.ServiceType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPublicationByIdUseCaseTest {

    private lateinit var publicationRepository: PublicationRepository
    private lateinit var externalServiceRepository: ExternalServiceRepository
    private lateinit var useCase: GetPublicationByIdUseCase

    @Before
    fun setUp() {
        publicationRepository = mockk()
        externalServiceRepository = mockk()
        useCase = GetPublicationByIdUseCase(publicationRepository, externalServiceRepository)
    }

    @Test
    fun `invoke should call external repository when category is external`() = runTest {
        // Arrange
        val id = "ext123"
        val publication = mockk<PublicationEntity>()
        coEvery { externalServiceRepository.getExternalServiceById(id) } returns Result.success(publication)

        // Act
        val result = useCase(id, ServiceType.EXTERNAL.value)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { externalServiceRepository.getExternalServiceById(id) }
        coVerify(exactly = 0) { publicationRepository.getPublicationById(any()) }
    }

    @Test
    fun `invoke should call publication repository when category is not external`() = runTest {
        // Arrange
        val id = "pub123"
        val publication = mockk<PublicationEntity>()
        coEvery { publicationRepository.getPublicationById(id) } returns Result.success(publication)

        // Act
        val result = useCase(id, "internal")

        // Assert
        assertTrue(result.isSuccess)
        coVerify { publicationRepository.getPublicationById(id) }
        coVerify(exactly = 0) { externalServiceRepository.getExternalServiceById(any()) }
    }
}
