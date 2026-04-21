package com.example.condospace.presentation.ui.feature.favorites.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetFavoritePublicationsUseCase
import com.example.condospace.presentation.ui.feature.favorites.state.FavoritesUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getFavoritePublicationsUseCase: GetFavoritePublicationsUseCase
    private lateinit var viewModel: FavoritesViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        getFavoritePublicationsUseCase = mockk()

        coEvery { userPreferencesRepository.userData } returns userDataFlow

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
    fun `viewModel should load favorites when userData is emitted`() = runTest(testDispatcher) {
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            publicationsIdFavored = listOf("pub1")
        )
        val publications = listOf(mockk<PublicationEntity>(relaxed = true))
        val deferred = CompletableDeferred<Result<List<PublicationEntity>>>()
        
        coEvery { getFavoritePublicationsUseCase("user123", listOf("pub1")) } coAnswers { deferred.await() }

        userDataFlow.value = user
        runCurrent()

        assertTrue(viewModel.uiState.value is FavoritesUiState.Success)
        val loadingState = viewModel.uiState.value as FavoritesUiState.Success
        assertTrue(loadingState.isListLoading)

        deferred.complete(Result.success(publications))
        advanceUntilIdle()

        val successState = viewModel.uiState.value as FavoritesUiState.Success
        assertFalse(successState.isListLoading)
        assertEquals(1, successState.publications.size)
    }

    @Test
    fun `viewModel should show error when user is null`() = runTest(testDispatcher) {
        userDataFlow.value = null
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is FavoritesUiState.Error)
        assertEquals("Usuário não encontrado", (viewModel.uiState.value as FavoritesUiState.Error).message)
    }

    @Test
    fun `viewModel should show empty list when favoriteIds is empty`() = runTest(testDispatcher) {
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            publicationsIdFavored = emptyList()
        )
        
        userDataFlow.value = user
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is FavoritesUiState.Success)
        val state = viewModel.uiState.value as FavoritesUiState.Success
        assertTrue(state.publications.isEmpty())
        assertFalse(state.isListLoading)
    }

    @Test
    fun `viewModel should handle failure in loadFavorites`() = runTest(testDispatcher) {
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            publicationsIdFavored = listOf("pub1")
        )
        
        coEvery { getFavoritePublicationsUseCase("user123", listOf("pub1")) } returns Result.failure(Exception("Load Error"))

        userDataFlow.value = user
        advanceUntilIdle()

        val state = viewModel.uiState.value as FavoritesUiState.Success
        assertEquals("Load Error", state.actionError)
        assertFalse(state.isListLoading)
    }
}
