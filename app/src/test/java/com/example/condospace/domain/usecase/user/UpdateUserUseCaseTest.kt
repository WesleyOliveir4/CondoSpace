package com.example.condospace.domain.usecase.user

import com.example.condospace.domain.entity.UserEntity
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

class UpdateUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var useCase: UpdateUserUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        userPreferencesRepository = mockk(relaxed = true)
        useCase = UpdateUserUseCase(userRepository, userPreferencesRepository)
    }

    @Test
    fun `invoke should update firebase and then local dataStore when success`() = runTest {
        // Arrange
        val user = mockk<UserEntity>()
        coEvery { userRepository.updateUser(user) } returns Result.success(Unit)

        // Act
        val result = useCase(user)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { 
            userRepository.updateUser(user)
            userPreferencesRepository.saveUserData(user)
        }
    }

    @Test
    fun `invoke should not update local dataStore when firebase fails`() = runTest {
        // Arrange
        val user = mockk<UserEntity>()
        val exception = Exception("Firebase Error")
        coEvery { userRepository.updateUser(user) } returns Result.failure(exception)

        // Act
        val result = useCase(user)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { 
            userPreferencesRepository.saveUserData(any())
        }
    }
}
