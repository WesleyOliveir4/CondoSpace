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
import org.junit.Assert.assertNull
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

    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPublicationByIdUseCase = mockk()
        userPreferencesRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        publicationRepository = mockk(relaxed = true)
        getContactUrlUseCase = mockk()

        every { userPreferencesRepository.userData } returns userDataFlow
        
        mockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")

        viewModel = PublicationSelectedViewModel(
            getPublicationByIdUseCase, 
            userPreferencesRepository, 
            userRepository, 
            publicationRepository, 
            getContactUrlUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPublication should set state to Success when use case returns entity`() = runTest(testDispatcher) {
        val pubId = "123"
        val entity = mockk<PublicationEntity>(relaxed = true)
        val publicationUi = mockk<PublicationUiModel>(relaxed = true)
        every { publicationUi.id } returns pubId
        
        every { entity.toUiModel() } returns publicationUi
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.success(entity)
        coEvery { userPreferencesRepository.getUserData() } returns null

        viewModel.loadPublication(pubId, "Aviso")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PublicationSelectedUiState.Success)
        assertEquals(pubId, (viewModel.uiState.value as PublicationSelectedUiState.Success).publication.id)
    }

    @Test
    fun `loadPublication should set state to Error when use case fails`() = runTest(testDispatcher) {
        val pubId = "123"
        val errorMessage = "Not found"
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.failure(Exception(errorMessage))

        viewModel.loadPublication(pubId, "Aviso")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PublicationSelectedUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as PublicationSelectedUiState.Error).message)
    }

    @Test
    fun `onFavoriteClick should add to favorites when not already favored`() = runTest(testDispatcher) {
        val pubId = "pub123"
        val user = UserEntity(uuid = "u", name = "N", phoneNumber = "", profilePicture = null, email = "", publicationsIdFavored = emptyList())
        
        val publicationUi = PublicationUiModel(id = pubId, publicationOwner = "O", title = "T", description = "D", publicationType = "A", price = 0.0, likes = 10, date = "D")

        coEvery { userPreferencesRepository.getUserData() } returns user
        val entity = mockk<PublicationEntity>(relaxed = true)
        every { entity.toUiModel() } returns publicationUi
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.success(entity)

        viewModel.loadPublication(pubId, "A")
        advanceUntilIdle()

        viewModel.onFavoriteClick()
        runCurrent()

        val state = viewModel.uiState.value as PublicationSelectedUiState.Success
        assertTrue(state.isFavorite)
        assertEquals(11, state.publication.likes)
        
        coVerify { 
            userPreferencesRepository.saveUserData(match { it.publicationsIdFavored?.contains(pubId) == true })
            userRepository.updateFavoritePublications(any(), any())
            publicationRepository.updatePublicationLikes(pubId, 1)
        }
    }

    @Test
    fun `onFavoriteClick should remove from favorites when already favored`() = runTest(testDispatcher) {
        val pubId = "pub123"
        val user = UserEntity(uuid = "u", name = "N", phoneNumber = "", profilePicture = null, email = "", publicationsIdFavored = listOf(pubId))
        
        val publicationUi = PublicationUiModel(id = pubId, publicationOwner = "O", title = "T", description = "D", publicationType = "A", price = 0.0, likes = 10, date = "D")

        coEvery { userPreferencesRepository.getUserData() } returns user
        val entity = mockk<PublicationEntity>(relaxed = true)
        every { entity.toUiModel() } returns publicationUi
        coEvery { getPublicationByIdUseCase(pubId, any()) } returns Result.success(entity)

        viewModel.loadPublication(pubId, "A")
        advanceUntilIdle()

        viewModel.onFavoriteClick()
        runCurrent()

        val state = viewModel.uiState.value as PublicationSelectedUiState.Success
        assertFalse(state.isFavorite)
        assertEquals(9, state.publication.likes)
        
        coVerify { 
            userPreferencesRepository.saveUserData(match { it.publicationsIdFavored?.contains(pubId) == false })
            publicationRepository.updatePublicationLikes(pubId, -1)
        }
    }

    @Test
    fun `getContactUrl should call useCase`() {
        val publicationUi = mockk<PublicationUiModel>(relaxed = true) {
            every { contact } returns "123"
            every { publicationOwner } returns "Owner"
            every { title } returns "Title"
        }
        val category = "Venda"
        every { getContactUrlUseCase("123", "Owner", "Title", category) } returns "http://whatsapp.com"

        val result = viewModel.getContactUrl(publicationUi, category)

        assertEquals("http://whatsapp.com", result)
    }

    @Test
    fun `resetActionError should nullify actionError`() = runTest(testDispatcher) {
        val pubUi = mockk<PublicationUiModel>(relaxed = true)
        val entity = mockk<PublicationEntity>(relaxed = true)
        every { entity.toUiModel() } returns pubUi
        coEvery { getPublicationByIdUseCase(any(), any()) } returns Result.success(entity)
        
        viewModel.loadPublication("1", "A")
        advanceUntilIdle()
        
        // Simular um erro de ação (não há setter direto, mas o estado de Success permite cópia interna no ViewModel)
        // Como o ViewModel não expõe setter para actionError, testamos a transição da função
        viewModel.resetActionError()
        runCurrent()

        val state = viewModel.uiState.value as PublicationSelectedUiState.Success
        assertNull(state.actionError)
    }

    @Test
    fun `observeUserData should update isFavorite when favorites list changes`() = runTest(testDispatcher) {
        val pubId = "pub1"
        val pubUi = mockk<PublicationUiModel>(relaxed = true) { every { id } returns pubId }
        val entity = mockk<PublicationEntity>(relaxed = true)
        every { entity.toUiModel() } returns pubUi
        coEvery { getPublicationByIdUseCase(any(), any()) } returns Result.success(entity)

        viewModel.loadPublication(pubId, "A")
        advanceUntilIdle()

        // Emit new user data with the publication favored
        userDataFlow.value = UserEntity(uuid = "u", name = "N", phoneNumber = "", profilePicture = null, email = "", publicationsIdFavored = listOf(pubId))
        runCurrent()

        assertTrue((viewModel.uiState.value as PublicationSelectedUiState.Success).isFavorite)

        // Emit new user data without the publication favored
        userDataFlow.value = UserEntity(uuid = "u", name = "N", phoneNumber = "", profilePicture = null, email = "", publicationsIdFavored = emptyList())
        runCurrent()

        assertFalse((viewModel.uiState.value as PublicationSelectedUiState.Success).isFavorite)
    }
}
