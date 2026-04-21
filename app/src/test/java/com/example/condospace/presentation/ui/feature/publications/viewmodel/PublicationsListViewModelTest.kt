package com.example.condospace.presentation.ui.feature.publications.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumAndTypeUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationsListUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PublicationsListViewModelTest {

    private lateinit var getPublicationsByCondominiumAndTypeUseCase: GetPublicationsByCondominiumAndTypeUseCase
    private lateinit var viewModel: PublicationsListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPublicationsByCondominiumAndTypeUseCase = mockk()
        
        mockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")

        viewModel = PublicationsListViewModel(
            getPublicationsByCondominiumAndTypeUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPublications should update state to Success when useCase returns list`() = runTest(testDispatcher) {
        val category = "Venda"
        val ids = listOf("1", "2")
        val pubEntity = mockk<PublicationEntity>(relaxed = true)
        val pubUi = mockk<PublicationUiModel>(relaxed = true)
        
        every { pubEntity.toUiModel() } returns pubUi
        coEvery { getPublicationsByCondominiumAndTypeUseCase(category, ids) } returns Result.success(listOf(pubEntity))

        viewModel.loadPublications(category, ids)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PublicationsListUiState.Success)
        val state = viewModel.uiState.value as PublicationsListUiState.Success
        assertEquals(1, state.publications.size)
    }

    @Test
    fun `loadPublications should update state to Error when useCase fails`() = runTest(testDispatcher) {
        val category = "Venda"
        val ids = listOf("1", "2")
        val errorMessage = "Erro de conexão"
        
        coEvery { getPublicationsByCondominiumAndTypeUseCase(category, ids) } returns Result.failure(Exception(errorMessage))

        viewModel.loadPublications(category, ids)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PublicationsListUiState.Error)
        val state = viewModel.uiState.value as PublicationsListUiState.Error
        assertEquals(errorMessage, state.message)
    }

    @Test
    fun `loadPublications should update state to Error with default message when exception message is null`() = runTest(testDispatcher) {
        val category = "Venda"
        val ids = listOf("1", "2")
        
        coEvery { getPublicationsByCondominiumAndTypeUseCase(category, ids) } returns Result.failure(Exception())

        viewModel.loadPublications(category, ids)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PublicationsListUiState.Error)
        val state = viewModel.uiState.value as PublicationsListUiState.Error
        assertEquals("Erro ao carregar publicações", state.message)
    }
}
