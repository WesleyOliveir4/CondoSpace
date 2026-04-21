package com.example.condospace.presentation.ui.feature.profile.viewmodel

import android.net.Uri
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.user.GetUserUseCase
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.profile.state.UserDataUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDataViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var updateUserUseCase: UpdateUserUseCase
    private lateinit var imageRepository: ImageRepository
    private lateinit var publicationRepository: PublicationRepository
    private lateinit var viewModel: UserDataViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk(relaxed = true)
        getUserUseCase = mockk()
        updateUserUseCase = mockk()
        imageRepository = mockk()
        publicationRepository = mockk()

        every { userPreferencesRepository.userData } returns flowOf(null)
        mockkStatic("com.example.condospace.presentation.model.UserUiModelKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.UserUiModelKt")
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUserFirebaseData should set Success state when user found`() = runTest {
        // Arrange
        val userId = "user123"
        val userEntity = mockk<UserEntity>(relaxed = true)
        val userUi = mockk<UserUiModel>(relaxed = true)
        
        every { userEntity.uuid } returns userId
        every { userEntity.toUiModel() } returns userUi
        coEvery { getUserUseCase(any()) } returns Result.success(userEntity)
        every { userPreferencesRepository.userData } returns flowOf(userEntity)

        // Act
        viewModel = UserDataViewModel(
            userPreferencesRepository, getUserUseCase, updateUserUseCase, imageRepository, publicationRepository
        )

        // Assert
        assertTrue(viewModel.uiState.value is UserDataUiState.Success)
        assertEquals(userUi, (viewModel.uiState.value as UserDataUiState.Success).user)
    }

    @Test
    fun `updateUserName should call updateUseCase and update publications`() = runTest {
        // Arrange
        val userId = "user123"
        val initialUser = UserUiModel(uuid = userId, name = "Old Name", email = "", phoneNumber = "")
        val updatedUser = initialUser.copy(name = "New Name")
        
        val userEntity = mockk<UserEntity>(relaxed = true)
        every { userEntity.uuid } returns userId
        every { userEntity.toUiModel() } returns initialUser
        coEvery { getUserUseCase(userId) } returns Result.success(userEntity)
        every { userPreferencesRepository.userData } returns flowOf(userEntity)
        
        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)
        coEvery { publicationRepository.getPublicationsByUser(userId) } returns flowOf(Result.success(emptyList()))

        viewModel = UserDataViewModel(
            userPreferencesRepository, getUserUseCase, updateUserUseCase, imageRepository, publicationRepository
        )

        // Act
        viewModel.updateUserName("New Name")

        // Assert
        coVerify { updateUserUseCase(any()) }
        val state = viewModel.uiState.value as UserDataUiState.Success
        assertEquals("New Name", state.user.name)
    }

    @Test
    fun `updateProfilePicture should upload image and then update user`() = runTest {
        // Arrange
        val userId = "user123"
        val uri = mockk<Uri>()
        val initialUser = UserUiModel(uuid = userId, name = "Name", email = "", phoneNumber = "")
        val imageUrl = "http://image.url"
        
        val userEntity = mockk<UserEntity>(relaxed = true)
        every { userEntity.uuid } returns userId
        every { userEntity.toUiModel() } returns initialUser
        coEvery { getUserUseCase(userId) } returns Result.success(userEntity)
        every { userPreferencesRepository.userData } returns flowOf(userEntity)
        
        coEvery { imageRepository.uploadImages(any(), any()) } returns Result.success(listOf(PublicationImageEntity(imageUrl, "id")))
        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)

        viewModel = UserDataViewModel(
            userPreferencesRepository, getUserUseCase, updateUserUseCase, imageRepository, publicationRepository
        )

        // Act
        viewModel.updateProfilePicture(uri)

        // Assert
        coVerify { imageRepository.uploadImages(any(), any()) }
        coVerify { updateUserUseCase(match { it.profilePicture == imageUrl }) }
    }
}
