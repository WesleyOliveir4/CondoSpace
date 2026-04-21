package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.CondominiumRepository
import com.example.condospace.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveCondominiumUseCaseTest {

    private lateinit var condominiumRepository: CondominiumRepository
    private lateinit var userRepository: UserRepository
    private lateinit var useCase: SaveCondominiumUseCase

    @Before
    fun setUp() {
        condominiumRepository = mockk()
        userRepository = mockk()
        useCase = SaveCondominiumUseCase(condominiumRepository, userRepository)
    }

    @Test
    fun `invoke should return success when both repository calls succeed`() = runTest {
        // Arrange
        val userId = "user123"
        val condo = mockk<CondominiumEntity>()
        coEvery { condominiumRepository.saveCondominium(condo) } returns Result.success(Unit)
        coEvery { userRepository.updateUserCondominium(userId, condo) } returns Result.success(Unit)

        // Act
        val result = useCase(userId, condo)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when condominium repository fails`() = runTest {
        // Arrange
        val userId = "user123"
        val condo = mockk<CondominiumEntity>()
        val exception = Exception("Save condo error")
        coEvery { condominiumRepository.saveCondominium(condo) } returns Result.failure(exception)

        // Act
        val result = useCase(userId, condo)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should return failure when user repository fails`() = runTest {
        // Arrange
        val userId = "user123"
        val condo = mockk<CondominiumEntity>()
        val exception = Exception("Update user error")
        coEvery { condominiumRepository.saveCondominium(condo) } returns Result.success(Unit)
        coEvery { userRepository.updateUserCondominium(userId, condo) } returns Result.failure(exception)

        // Act
        val result = useCase(userId, condo)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
