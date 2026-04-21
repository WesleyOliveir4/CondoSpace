package com.example.condospace.presentation.ui.feature.favorites.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetFavoritePublicationsUseCase
import com.example.condospace.presentation.ui.feature.favorites.state.FavoritesUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class FavoritesViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getFavoritePublicationsUseCase: GetFavoritePublicationsUseCase
    private lateinit var viewModel: FavoritesViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    private fun createPublicationEntity(id: String = "1") = PublicationEntity(
        id = id,
        publicationOwnerUuid = "uid",
        publicationCondominiumId = "condoId",
        publicationOwner = "Owner",
        title = "Title",
        description = "Desc",
        publicationType = "Type",
        price = 10.0,
        likes = 0,
        date = "2023-10-10"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        getFavoritePublicationsUseCase = mockk()

        every { userPreferencesRepository.userData } returns userDataFlow

        viewModel = FavoritesViewModel(
            userPreferencesRepository,
            getFavoritePublicationsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observeUserData should update state to Success when user data is available`() = runTest {
        // Arrange
        val userEntity = UserEntity(
            uuid = "user123",
            name = "Test User",
            email = "test@test.com",
            phoneNumber = "",
            profilePicture = null,
            publicationsIdFavored = listOf("1"),
            userIsLogged = true
        )
        val publications = listOf(createPublicationEntity(id = "1"))
        coEvery { getFavoritePublicationsUseCase("user123", listOf("1")) } returns Result.success(publications)

        // Act
        userDataFlow.value = userEntity

        // Assert
        assertTrue(viewModel.uiState.value is FavoritesUiState.Success)
        val state = viewModel.uiState.value as FavoritesUiState.Success
        assertEquals("user123", state.user.uuid)
        assertEquals(1, state.publications.size)
        assertEquals("1", state.publications[0].id)
    }

    @Test
    fun `observeUserData should update state to Error when user is null`() = runTest {
        // Act
        userDataFlow.value = null

        // Assert
        assertTrue(viewModel.uiState.value is FavoritesUiState.Error)
        assertEquals("Usuário não encontrado", (viewModel.uiState.value as FavoritesUiState.Error).message)
    }

    @Test
    fun `loadFavorites should handle empty favorite list`() = runTest {
        // Arrange
        val userEntity = UserEntity(
            uuid = "user123",
            name = "Test User",
            email = "test@test.com",
            phoneNumber = "",
            profilePicture = null,
            publicationsIdFavored = emptyList(),
            userIsLogged = true
        )

        // Act
        userDataFlow.value = userEntity

        // Assert
        val state = viewModel.uiState.value as FavoritesUiState.Success
        assertTrue(state.publications.isEmpty())
        assertEquals(false, state.isListLoading)
    }

    @Test
    fun `loadFavorites should update actionError when use case fails`() = runTest {
        // Arrange
        val userEntity = UserEntity(
            uuid = "user123",
            name = "Test User",
            email = "test@test.com",
            phoneNumber = "",
            profilePicture = null,
            publicationsIdFavored = listOf("1"),
            userIsLogged = true
        )
        val errorMessage = "Error loading favorites"
        coEvery { getFavoritePublicationsUseCase("user123", listOf("1")) } returns Result.failure(Exception(errorMessage))

        // Act
        userDataFlow.value = userEntity

        // Assert
        val state = viewModel.uiState.value as FavoritesUiState.Success
        assertEquals(errorMessage, state.actionError)
        assertEquals(false, state.isListLoading)
    }
}
