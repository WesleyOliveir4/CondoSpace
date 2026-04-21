package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.CondominiumRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchCondominiumByCepUseCaseTest {

    private lateinit var repository: CondominiumRepository
    private lateinit var useCase: SearchCondominiumByCepUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchCondominiumByCepUseCase(repository)
    }

    @Test
    fun `invoke should return empty list immediately if cep length is less than 8`() = runTest {
        // Act
        val result = useCase("12345")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `invoke should call repository when cep length is 8`() = runTest {
        // Arrange
        val cep = "12345678"
        val condos = listOf(mockk<CondominiumEntity>())
        coEvery { repository.searchByCep(cep) } returns Result.success(condos)

        // Act
        val result = useCase(cep)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(condos, result.getOrNull())
    }
}
