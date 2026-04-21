package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserCondominiumUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: GetUserCondominiumUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        useCase = GetUserCondominiumUseCase(userRepository)
    }

    @Test
    fun `invoke should return condominium entity when user has valid condominium data`() = runTest {
        // Arrange
        val userId = "user123"
        val condominium = CondominiumEntity(id = "condo1", name = "Condo A", cep = "12345-678")
        val user = mockk<UserEntity>()
        every { user.condominiumEntity } returns condominium
        
        coEvery { userRepository.getUser(userId) } returns Result.success(user)

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(condominium, result.getOrNull())
    }

    @Test
    fun `invoke should return null when user has no condominium`() = runTest {
        // Arrange
        val userId = "user123"
        val user = mockk<UserEntity>()
        every { user.condominiumEntity } returns null
        
        coEvery { userRepository.getUser(userId) } returns Result.success(user)

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `invoke should return null when condominium data is invalid`() = runTest {
        // Arrange
        val userId = "user123"
        val user = mockk<UserEntity>()
        // Invalid: name is blank or cep is blank
        val invalidCondo = CondominiumEntity(name = "", cep = " ")
        every { user.condominiumEntity } returns invalidCondo
        
        coEvery { userRepository.getUser(userId) } returns Result.success(user)

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Network error")
        coEvery { userRepository.getUser(userId) } returns Result.failure(exception)

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
