package com.example.condospace.domain.usecase.register

import com.example.condospace.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignUpUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        signUpUseCase = SignUpUseCase(authRepository)
    }

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val uid = "user_uid"
        coEvery { authRepository.signUp(email, password) } returns Result.success(uid)

        // Act
        val result = signUpUseCase(email, password)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(uid, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository returns failure`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Sign up error")
        coEvery { authRepository.signUp(email, password) } returns Result.failure(exception)

        // Act
        val result = signUpUseCase(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
