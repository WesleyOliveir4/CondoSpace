package com.example.condospace.domain.usecase.user

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: GetUserUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        useCase = GetUserUseCase(userRepository)
    }

    @Test
    fun `invoke should return user from repository`() = runTest {
        // Arrange
        val userId = "user123"
        val user = mockk<UserEntity>()
        coEvery { userRepository.getUser(userId) } returns Result.success(user)

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Arrange
        val userId = "user123"
        val exception = Exception("Error")
        coEvery { userRepository.getUser(userId) } returns Result.failure(exception)

        // Act
        val result = useCase(userId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
