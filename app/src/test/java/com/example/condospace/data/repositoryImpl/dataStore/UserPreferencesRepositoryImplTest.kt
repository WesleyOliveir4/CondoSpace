package com.example.condospace.data.repositoryImpl.dataStore

import androidx.datastore.core.DataStore
import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.UserEntity
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserPreferencesRepositoryImplTest {

    private lateinit var dataStore: DataStore<User?>
    private lateinit var repository: UserPreferencesRepositoryImpl
    private val dataFlow = MutableStateFlow<User?>(null)

    @Before
    fun setUp() {
        dataStore = mockk()
        // Stubbing 'data' before repository creation because it's used in property initialization
        every { dataStore.data } returns dataFlow
        repository = UserPreferencesRepositoryImpl(dataStore)
    }

    @Test
    fun `userData flow should emit mapped user entity`() = runTest {
        // Arrange
        val userModel = User(uuid = "123", name = "Test", email = "test@test.com", phoneNumber = "")
        dataFlow.value = userModel

        // Act
        val result = repository.userData.first()

        // Assert
        assertEquals("123", result?.uuid)
        assertEquals("Test", result?.name)
    }

    @Test
    fun `userData flow should emit null when dataStore is null`() = runTest {
        // Arrange
        dataFlow.value = null

        // Act
        val result = repository.userData.first()

        // Assert
        assertNull(result)
    }

    @Test
    fun `saveUserData should call updateData on dataStore`() = runTest {
        // Arrange
        val userEntity = UserEntity(
            uuid = "123",
            name = "Test",
            email = "test@test.com",
            phoneNumber = "",
            profilePicture = null,
            publicationsIdFavored = emptyList(),
            userIsLogged = true
        )
        
        // Mock updateData to execute the transform lambda
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (User?) -> User?>()
            val result = transform(null)
            assertEquals("123", result?.uuid)
            result
        }

        // Act
        repository.saveUserData(userEntity)

        // Assert
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `clearUserData should set data to null in dataStore`() = runTest {
        // Arrange
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (User?) -> User?>()
            val result = transform(mockk())
            assertNull(result)
            null
        }

        // Act
        repository.clearUserData()

        // Assert
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `getUserData should return mapped entity from dataStore`() = runTest {
        // Arrange
        val userModel = User(uuid = "123", name = "Test", email = "test@test.com", phoneNumber = "")
        dataFlow.value = userModel

        // Act
        val result = repository.getUserData()

        // Assert
        assertEquals("123", result?.uuid)
    }
}
