package com.example.condospace.presentation.ui.feature.condominium.viewmodel

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.usecase.condominium.GetUserCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.SaveCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.SearchCondominiumByCepUseCase
import com.example.condospace.domain.usecase.condominium.UpdateUserCondominiumUseCase
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.ui.feature.condominium.state.CondominiumState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SelectCondominiumViewModelTest {

    private lateinit var getUserCondominiumUseCase: GetUserCondominiumUseCase
    private lateinit var searchCondominiumByCepUseCase: SearchCondominiumByCepUseCase
    private lateinit var saveCondominiumUseCase: SaveCondominiumUseCase
    private lateinit var updateUserCondominiumUseCase: UpdateUserCondominiumUseCase
    private lateinit var viewModel: SelectCondominiumViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getUserCondominiumUseCase = mockk()
        searchCondominiumByCepUseCase = mockk()
        saveCondominiumUseCase = mockk()
        updateUserCondominiumUseCase = mockk()

        viewModel = SelectCondominiumViewModel(
            getUserCondominiumUseCase,
            searchCondominiumByCepUseCase,
            saveCondominiumUseCase,
            updateUserCondominiumUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUserCondominium should update state to Success when successful`() = runTest {
        // Arrange
        val userId = "user123"
        val condoEntity = CondominiumEntity(id = "condo123", name = "Condo Test", cep = "12345678")
        coEvery { getUserCondominiumUseCase(userId) } returns Result.success(condoEntity)

        // Act
        viewModel.fetchUserCondominium(userId)

        // Assert
        assertTrue(viewModel.condominiumState.value is CondominiumState.Success)
        assertEquals("condo123", (viewModel.condominiumState.value as CondominiumState.Success).selectedCondominium?.id)
    }

    @Test
    fun `fetchUserCondominium should update state to Error when fails`() = runTest {
        // Arrange
        val userId = "user123"
        val errorMessage = "Error fetching"
        coEvery { getUserCondominiumUseCase(userId) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.fetchUserCondominium(userId)

        // Assert
        assertTrue(viewModel.condominiumState.value is CondominiumState.Error)
        assertEquals(errorMessage, (viewModel.condominiumState.value as CondominiumState.Error).message)
    }

    @Test
    fun `searchCondominiumByCep should update searchResults when successful`() = runTest {
        // Arrange
        val cep = "12345678"
        val condoList = listOf(CondominiumEntity(id = "1", name = "Condo 1", cep = cep))
        coEvery { searchCondominiumByCepUseCase(cep) } returns Result.success(condoList)

        // Act
        viewModel.searchCondominiumByCep(cep)

        // Assert
        assertEquals(1, viewModel.searchResults.value.size)
        assertEquals("Condo 1", viewModel.searchResults.value[0].name)
        assertEquals(false, viewModel.isSearching.value)
    }

    @Test
    fun `saveCondominiumSelected should update success state when successful`() = runTest {
        // Arrange
        val userId = "user123"
        val condoUi = CondominiumUiModel(id = "condo123", name = "Condo Test", cep = "12345678")
        coEvery { updateUserCondominiumUseCase(userId, any()) } returns Result.success(Unit)

        // Act
        viewModel.saveCondominiumSelected(userId, condoUi)

        // Assert
        val state = viewModel.condominiumState.value as CondominiumState.Success
        assertTrue(state.saveSuccess)
        assertEquals("condo123", state.selectedCondominium?.id)
    }
}
