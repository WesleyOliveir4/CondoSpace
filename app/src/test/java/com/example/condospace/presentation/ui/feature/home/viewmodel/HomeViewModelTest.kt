package com.example.condospace.presentation.ui.feature.home.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumUseCase
import com.example.condospace.presentation.ui.feature.home.state.HomeUiState
import com.example.condospace.presentation.ui.enums.ServiceType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
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
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getPublicationsByCondominiumUseCase: GetPublicationsByCondominiumUseCase
    private lateinit var externalServiceRepository: ExternalServiceRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        getPublicationsByCondominiumUseCase = mockk()
        externalServiceRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should observe user data and fetch home content when condominium exists`() = runTest {
        // Arrange
        val userEntity = mockk<UserEntity>(relaxed = true)
        val condoId = "condo123"
        val cep = "12345678"
        
        every { userEntity.condominiumEntity?.id } returns condoId
        every { userEntity.condominiumEntity?.cep } returns cep
        every { userPreferencesRepository.userData } returns flowOf(userEntity)
        
        coEvery { getPublicationsByCondominiumUseCase(condoId) } returns Result.success(emptyList())
        coEvery { externalServiceRepository.getNearbyServices(cep) } returns Result.success(emptyList())

        // Act
        viewModel = HomeViewModel(userPreferencesRepository, getPublicationsByCondominiumUseCase, externalServiceRepository)

        // Assert
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(condoId, state.user.condominium?.id)
    }

    @Test
    fun `init should set error state when user not found`() = runTest {
        // Arrange
        every { userPreferencesRepository.userData } returns flowOf(null)

        // Act
        viewModel = HomeViewModel(userPreferencesRepository, getPublicationsByCondominiumUseCase, externalServiceRepository)

        // Assert
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        assertEquals("Usuário não encontrado", (viewModel.uiState.value as HomeUiState.Error).message)
    }

    @Test
    fun `refreshPublications should trigger new fetch`() = runTest {
        // Arrange
        val userEntity = mockk<UserEntity>(relaxed = true)
        val condoId = "condo123"
        every { userEntity.condominiumEntity?.id } returns condoId
        every { userPreferencesRepository.userData } returns flowOf(userEntity)
        coEvery { getPublicationsByCondominiumUseCase(condoId) } returns Result.success(emptyList())
        coEvery { externalServiceRepository.getNearbyServices(any()) } returns Result.success(emptyList())

        viewModel = HomeViewModel(userPreferencesRepository, getPublicationsByCondominiumUseCase, externalServiceRepository)

        // Act
        viewModel.refreshPublications()

        // Assert
        coEvery { getPublicationsByCondominiumUseCase(condoId) }
    }

    @Test
    fun `fetchHomeContent should group publications by type`() = runTest {
        // Arrange
        val userEntity = mockk<UserEntity>(relaxed = true)
        val condoId = "condo1"
        every { userEntity.condominiumEntity?.id } returns condoId
        every { userPreferencesRepository.userData } returns flowOf(userEntity)

        val pubService = mockk<PublicationEntity>(relaxed = true) {
            every { publicationType } returns ServiceType.SERVICE.value
        }
        val pubRecommendation = mockk<PublicationEntity>(relaxed = true) {
            every { publicationType } returns ServiceType.RECOMMENDATION.value
        }
        val pubProduct = mockk<PublicationEntity>(relaxed = true) {
            every { publicationType } returns "Outro"
        }

        coEvery { getPublicationsByCondominiumUseCase(condoId) } returns Result.success(listOf(pubService, pubRecommendation, pubProduct))
        coEvery { externalServiceRepository.getNearbyServices(any()) } returns Result.success(emptyList())

        // Act
        viewModel = HomeViewModel(userPreferencesRepository, getPublicationsByCondominiumUseCase, externalServiceRepository)

        // Assert
        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(1, state.publicationsService.size)
        assertEquals(1, state.publicationsRecommendation.size)
        assertEquals(1, state.publicationsProducts.size)
    }
}
