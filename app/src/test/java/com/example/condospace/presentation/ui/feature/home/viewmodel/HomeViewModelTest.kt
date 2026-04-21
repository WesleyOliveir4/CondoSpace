package com.example.condospace.presentation.ui.feature.home.viewmodel

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.enums.CategoryType
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.ui.feature.home.state.HomeUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
class HomeViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getPublicationsByCondominiumUseCase: GetPublicationsByCondominiumUseCase
    private lateinit var externalServiceRepository: ExternalServiceRepository
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        getPublicationsByCondominiumUseCase = mockk()
        externalServiceRepository = mockk()

        coEvery { userPreferencesRepository.userData } returns userDataFlow
        
        mockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
        mockkStatic("com.example.condospace.presentation.model.ExternalServiceUiModelKt")

        viewModel = HomeViewModel(
            userPreferencesRepository,
            getPublicationsByCondominiumUseCase,
            externalServiceRepository
        )
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
        unmockkStatic("com.example.condospace.presentation.model.ExternalServiceUiModelKt")
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel should load content when user has condominium`() = runTest(testDispatcher) {
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            condominiumEntity = CondominiumEntity(id = "condo123", name = "My Condo", cep = "12345678")
        )
        val pubEntity = mockk<PublicationEntity>(relaxed = true) {
            every { publicationType } returns ServiceType.SERVICE.value
        }
        val pubUi = mockk<PublicationUiModel>(relaxed = true)
        
        every { pubEntity.toUiModel() } returns pubUi
        coEvery { getPublicationsByCondominiumUseCase("condo123") } returns Result.success(listOf(pubEntity))
        coEvery { externalServiceRepository.getNearbyServices("12345678") } returns Result.success(emptyList())

        userDataFlow.value = user
        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals("My Condo", state.condominiumName)
        assertEquals(1, state.publicationsService.size)
    }

    @Test
    fun `refreshPublications should trigger content reload`() = runTest(testDispatcher) {
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            condominiumEntity = CondominiumEntity(id = "condo123", name = "My Condo", cep = "12345678")
        )
        userDataFlow.value = user
        coEvery { getPublicationsByCondominiumUseCase("condo123") } returns Result.success(emptyList())
        coEvery { externalServiceRepository.getNearbyServices("12345678") } returns Result.success(emptyList())
        advanceUntilIdle()

        val deferred = CompletableDeferred<Result<List<PublicationEntity>>>()
        coEvery { getPublicationsByCondominiumUseCase("condo123") } coAnswers { deferred.await() }

        viewModel.refreshPublications()
        runCurrent()

        assertTrue((viewModel.uiState.value as HomeUiState.Success).isRefreshing)

        deferred.complete(Result.success(emptyList()))
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as HomeUiState.Success).isRefreshing)
    }

    @Test
    fun `getPublicationIdsByCategory should filter correctly`() = runTest(testDispatcher) {
        val user = UserEntity(uuid = "u", name = "n", phoneNumber = "", profilePicture = null, email = "", condominiumEntity = CondominiumEntity("c", "n", "0"))
        userDataFlow.value = user
        
        val pub1 = mockk<PublicationEntity>(relaxed = true) {
            every { id } returns "id1"
            every { publicationType } returns "Venda"
        }
        val pubUi1 = mockk<PublicationUiModel>(relaxed = true) {
            every { id } returns "id1"
            every { publicationType } returns "Venda"
        }
        
        every { pub1.toUiModel() } returns pubUi1
        coEvery { getPublicationsByCondominiumUseCase("c") } returns Result.success(listOf(pub1))
        coEvery { externalServiceRepository.getNearbyServices(any()) } returns Result.success(emptyList())
        
        advanceUntilIdle()

        val allIds = viewModel.getPublicationIdsByCategory(CategoryType.ALLTYPES.title)
        assertEquals(listOf("id1"), allIds)

        val specificIds = viewModel.getPublicationIdsByCategory("Venda")
        assertEquals(listOf("id1"), specificIds)

        val emptyIds = viewModel.getPublicationIdsByCategory("Outro")
        assertTrue(emptyIds.isEmpty())
    }

    @Test
    fun `handleFailure should update state with actionError when in Success state`() = runTest(testDispatcher) {
        val user = UserEntity(uuid = "u", name = "n", phoneNumber = "", profilePicture = null, email = "", condominiumEntity = CondominiumEntity("c", "n", "0"))
        userDataFlow.value = user
        coEvery { getPublicationsByCondominiumUseCase("c") } returns Result.failure(Exception("Network Error"))
        coEvery { externalServiceRepository.getNearbyServices(any()) } returns Result.success(emptyList())

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals("Network Error", state.actionError)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `observeUserData should show error when user is null`() = runTest(testDispatcher) {
        userDataFlow.value = null
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        assertEquals("Usuário não encontrado", (viewModel.uiState.value as HomeUiState.Error).message)
    }

    @Test
    fun `retry should reset state to Loading and re-observe`() = runTest(testDispatcher) {
        userDataFlow.value = null
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HomeUiState.Error)

        viewModel.retry()
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `clearHomeData should be called when condominium is null`() = runTest(testDispatcher) {
        val userWithoutCondo = UserEntity(uuid = "u", name = "n", phoneNumber = "", profilePicture = null, email = "", condominiumEntity = null)
        userDataFlow.value = userWithoutCondo
        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertTrue(state.publicationsProducts.isEmpty())
        assertEquals("Selecionar Condomínio", state.condominiumName)
    }
}
