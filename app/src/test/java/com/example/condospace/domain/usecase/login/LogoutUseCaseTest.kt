package com.example.condospace.domain.usecase.login

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LogoutUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        authRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        userPreferencesRepository = mockk(relaxed = true)
        logoutUseCase = LogoutUseCase(authRepository, userRepository, userPreferencesRepository)
    }

    @Test
    fun `invoke should return success and update repositories when user is logged in`() = runTest {
        // Arrange
        val user = UserEntity(
            uuid = "123",
            name = "Test",
            phoneNumber = "123456",
            profilePicture = null,
            email = "test@test.com",
            userIsLogged = true
        )
        coEvery { userPreferencesRepository.getUserData() } returns user
        coEvery { authRepository.signOut() } returns Result.success(Unit)

        // Act
        val result = logoutUseCase()

        // Assert
        assertTrue(result.isSuccess)
        coVerify { 
            userRepository.updateUser(match { !it.userIsLogged })
            userPreferencesRepository.saveUserData(match { !it.userIsLogged })
            authRepository.signOut()
        }
    }

    @Test
    fun `invoke should return success even if no user data exists in preferences`() = runTest {
        // Arrange
        coEvery { userPreferencesRepository.getUserData() } returns null
        coEvery { authRepository.signOut() } returns Result.success(Unit)

        // Act
        val result = logoutUseCase()

        // Assert
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { userRepository.updateUser(any()) }
        coVerify { authRepository.signOut() }
    }

    @Test
    fun `invoke should return failure when an exception occurs`() = runTest {
        // Arrange
        val exception = Exception("Logout failed")
        coEvery { userPreferencesRepository.getUserData() } throws exception

        // Act
        val result = logoutUseCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
