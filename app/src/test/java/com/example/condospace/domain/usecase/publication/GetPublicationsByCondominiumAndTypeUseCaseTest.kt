package com.example.condospace.domain.usecase.publication

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.presentation.ui.enums.ServiceType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPublicationsByCondominiumAndTypeUseCaseTest {

    private lateinit var repository: PublicationRepository
    private lateinit var externalServiceRepository: ExternalServiceRepository
    private lateinit var useCase: GetPublicationsByCondominiumAndTypeUseCase

    @Before
    fun setUp() {
        repository = mockk()
        externalServiceRepository = mockk()
        useCase = GetPublicationsByCondominiumAndTypeUseCase(repository, externalServiceRepository)
    }

    @Test
    fun `invoke should return external services when category is EXTERNAL`() = runTest {
        // Arrange
        val ids = listOf("1", "2")
        val publications = listOf(mockk<PublicationEntity>())
        coEvery { externalServiceRepository.getExternalServicesByIds(ids) } returns Result.success(publications)

        // Act
        val result = useCase(ServiceType.EXTERNAL.value, ids)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(publications, result.getOrNull())
    }

    @Test
    fun `invoke should return repository publications when category is not EXTERNAL`() = runTest {
        // Arrange
        val ids = listOf("1", "2")
        val publications = listOf(mockk<PublicationEntity>())
        coEvery { repository.getPublicationsByIds(ids) } returns Result.success(publications)

        // Act
        val result = useCase("AVISO", ids)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(publications, result.getOrNull())
    }
}
