package com.example.condospace.presentation.ui.feature.publications.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.publication.GetContactUrlUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationSelectedUiState
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
class PublicationSelectedViewModelTest {

    private lateinit var getPublicationByIdUseCase: GetPublicationByIdUseCase
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var userRepository: UserRepository
    private lateinit var publicationRepository: PublicationRepository
    private lateinit var getContactUrlUseCase: GetContactUrlUseCase
    private lateinit var viewModel: PublicationSelectedViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPublicationByIdUseCase = mockk()
        userPreferencesRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        publicationRepository = mockk(relaxed = true)
        getContactUrlUseCase = mockk()

        every { userPreferencesRepository.userData } returns flowOf(null)
        
        mockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPublication should set state to Success when use case returns entity`() = runTest {
        // Arrange
        val pubId = "123"
        val entity = mockk<PublicationEntity>(relaxed = true)
        val publicationUi = mockk<PublicationUiModel>(relaxed = true)
        every { publicationUi.id } returns pubId
        
        every { entity.toUiModel() } returns publicationUi
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.success(entity)
        coEvery { userPreferencesRepository.getUserData() } returns null

        viewModel = PublicationSelectedViewModel(
            getPublicationByIdUseCase, userPreferencesRepository, userRepository, publicationRepository, getContactUrlUseCase
        )

        // Act
        viewModel.loadPublication(pubId, "Aviso")

        // Assert
        assertTrue(viewModel.uiState.value is PublicationSelectedUiState.Success)
        assertEquals(pubId, (viewModel.uiState.value as PublicationSelectedUiState.Success).publication.id)
    }

    @Test
    fun `loadPublication should set state to Error when use case fails`() = runTest {
        // Arrange
        val pubId = "123"
        val errorMessage = "Not found"
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.failure(Exception(errorMessage))

        viewModel = PublicationSelectedViewModel(
            getPublicationByIdUseCase, userPreferencesRepository, userRepository, publicationRepository, getContactUrlUseCase
        )

        // Act
        viewModel.loadPublication(pubId, "Aviso")

        // Assert
        assertTrue(viewModel.uiState.value is PublicationSelectedUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as PublicationSelectedUiState.Error).message)
    }

    @Test
    fun `onFavoriteClick should update favorites in repositories when success`() = runTest {
        // Arrange
        val pubId = "pub123"
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            publicationsIdFavored = mutableListOf(),
            userIsLogged = true
        )
        
        val publicationUi = PublicationUiModel(
            id = pubId,
            publicationOwner = "Owner",
            title = "Title",
            description = "Desc",
            publicationType = "Aviso",
            price = 0.0,
            likes = 10,
            date = "2023-01-01"
        )

        coEvery { userPreferencesRepository.getUserData() } returns user
        
        val entity = mockk<PublicationEntity>(relaxed = true)
        every { entity.toUiModel() } returns publicationUi
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.success(entity)

        viewModel = PublicationSelectedViewModel(
            getPublicationByIdUseCase, userPreferencesRepository, userRepository, publicationRepository, getContactUrlUseCase
        )
        
        viewModel.loadPublication(pubId, "Aviso")

        // Act
        viewModel.onFavoriteClick()

        // Assert
        val finalState = viewModel.uiState.value as PublicationSelectedUiState.Success
        assertTrue(finalState.isFavorite)
        assertEquals(11, finalState.publication.likes)
        
        coVerify { 
            userPreferencesRepository.saveUserData(any())
            userRepository.updateFavoritePublications(any(), any())
            publicationRepository.updatePublicationLikes(pubId, 1)
        }
    }
}
