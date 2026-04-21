package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateUserCondominiumUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var useCase: UpdateUserCondominiumUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        userPreferencesRepository = mockk(relaxed = true)
        useCase = UpdateUserCondominiumUseCase(userRepository, userPreferencesRepository)
    }

    @Test
    fun `invoke should update user in repository and sync with preferences when successful`() = runTest {
        // Arrange
        val userId = "user123"
        val condominium = CondominiumEntity(id = "condo1", name = "New Condo", cep = "12345-678")
        val currentUser = UserEntity(uuid = userId, name = "User", email = "user@test.com", phoneNumber = "123", profilePicture = null)
        
        coEvery { userRepository.updateUserCondominium(userId, condominium) } returns Result.success(Unit)
        coEvery { userPreferencesRepository.getUserData() } returns currentUser

        // Act
        val result = useCase(userId, condominium)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userRepository.updateUserCondominium(userId, condominium) }
        coVerify { userPreferencesRepository.saveUserData(any()) }
    }

    @Test
    fun `invoke should not sync preferences when repository update fails`() = runTest {
        // Arrange
        val userId = "user123"
        val condominium = CondominiumEntity(id = "condo1", name = "New Condo", cep = "12345-678")
        val exception = Exception("Update failed")
        
        coEvery { userRepository.updateUserCondominium(userId, condominium) } returns Result.failure(exception)

        // Act
        val result = useCase(userId, condominium)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { userPreferencesRepository.saveUserData(any()) }
    }

    @Test
    fun `invoke should return success even if sync with preferences fails`() = runTest {
        // Arrange
        val userId = "user123"
        val condominium = CondominiumEntity(id = "condo1", name = "New Condo", cep = "12345-678")
        val currentUser = UserEntity(uuid = userId, name = "User", email = "user@test.com", phoneNumber = "123", profilePicture = null)
        
        coEvery { userRepository.updateUserCondominium(userId, condominium) } returns Result.success(Unit)
        coEvery { userPreferencesRepository.getUserData() } returns currentUser
        coEvery { userPreferencesRepository.saveUserData(any()) } throws Exception("DataStore error")

        // Act
        val result = useCase(userId, condominium)

        // Assert
        assertTrue(result.isSuccess)
    }
}
