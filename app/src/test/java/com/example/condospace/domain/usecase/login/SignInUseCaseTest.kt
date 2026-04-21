package com.example.condospace.domain.usecase.login

import com.example.condospace.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signInUseCase: SignInUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        signInUseCase = SignInUseCase(authRepository)
    }

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        coEvery { authRepository.signIn(email, password) } returns Result.success(Unit)

        // Act
        val result = signInUseCase(email, password)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository returns failure`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Sign in error")
        coEvery { authRepository.signIn(email, password) } returns Result.failure(exception)

        // Act
        val result = signInUseCase(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
