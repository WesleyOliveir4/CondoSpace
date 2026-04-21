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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SelectCondominiumViewModelTest {

    private lateinit var getUserCondominiumUseCase: GetUserCondominiumUseCase
    private lateinit var searchCondominiumByCepUseCase: SearchCondominiumByCepUseCase
    private lateinit var saveCondominiumUseCase: SaveCondominiumUseCase
    private lateinit var updateUserCondominiumUseCase: UpdateUserCondominiumUseCase
    private lateinit var viewModel: SelectCondominiumViewModel

    private val testDispatcher = StandardTestDispatcher()

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
    fun `fetchUserCondominium should update state to Success when found`() = runTest(testDispatcher) {
        val condoEntity = CondominiumEntity(id = "1", name = "Condo 1", cep = "12345")
        coEvery { getUserCondominiumUseCase("user123") } returns Result.success(condoEntity)

        viewModel.fetchUserCondominium("user123")
        advanceUntilIdle()

        val state = viewModel.condominiumState.value as CondominiumState.Success
        assertEquals("Condo 1", state.selectedCondominium?.name)
    }

    @Test
    fun `fetchUserCondominium should update state to Error on failure`() = runTest(testDispatcher) {
        coEvery { getUserCondominiumUseCase(any()) } returns Result.failure(Exception("Fetch Error"))

        viewModel.fetchUserCondominium("user123")
        advanceUntilIdle()

        assertTrue(viewModel.condominiumState.value is CondominiumState.Error)
        assertEquals("Fetch Error", (viewModel.condominiumState.value as CondominiumState.Error).message)
    }

    @Test
    fun `searchCondominiumByCep should update searchResults on success`() = runTest(testDispatcher) {
        val results = listOf(CondominiumEntity(id = "1", name = "Found", cep = "12345"))
        val deferred = CompletableDeferred<Result<List<CondominiumEntity>>>()
        coEvery { searchCondominiumByCepUseCase("12345") } coAnswers { deferred.await() }

        viewModel.searchCondominiumByCep("12345")
        
        runCurrent()
        assertTrue("isSearching should be true during search", viewModel.isSearching.value)
        
        deferred.complete(Result.success(results))
        advanceUntilIdle()
        
        assertFalse("isSearching should be false after search", viewModel.isSearching.value)
        assertEquals(1, viewModel.searchResults.value.size)
        assertEquals("Found", viewModel.searchResults.value[0].name)
    }

    @Test
    fun `searchCondominiumByCep should clear results on failure`() = runTest(testDispatcher) {
        coEvery { searchCondominiumByCepUseCase(any()) } returns Result.failure(Exception("API Error"))

        viewModel.searchCondominiumByCep("12345")
        advanceUntilIdle()

        assertFalse(viewModel.isSearching.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
    }

    @Test
    fun `saveCondominiumCreated should save and then select`() = runTest(testDispatcher) {
        val condoUi = CondominiumUiModel(id = "", name = "New", cep = "12345")
        coEvery { saveCondominiumUseCase(any(), any()) } returns Result.success(Unit)
        coEvery { updateUserCondominiumUseCase(any(), any()) } returns Result.success(Unit)

        viewModel.saveCondominiumCreated("user123", condoUi)
        advanceUntilIdle()

        val state = viewModel.condominiumState.value as CondominiumState.Success
        assertTrue(state.saveSuccess)
        assertEquals("New", state.selectedCondominium?.name)
    }

    @Test
    fun `saveCondominiumCreated should update error state on failure`() = runTest(testDispatcher) {
        val condoUi = CondominiumUiModel(id = "", name = "New", cep = "12345")
        coEvery { saveCondominiumUseCase(any(), any()) } returns Result.failure(Exception("Save Failed"))

        viewModel.saveCondominiumCreated("user123", condoUi)
        advanceUntilIdle()

        val state = viewModel.condominiumState.value as CondominiumState.Error
        assertEquals("Save Failed", state.message)
    }

    @Test
    fun `resetActionState should clear error and saveSuccess in Success state`() = runTest(testDispatcher) {
        val condoUi = CondominiumUiModel(id = "1", name = "Condo", cep = "123")
        coEvery { getUserCondominiumUseCase(any()) } returns Result.success(null)
        viewModel.fetchUserCondominium("user")
        advanceUntilIdle()
        
        coEvery { updateUserCondominiumUseCase(any(), any()) } returns Result.success(Unit)
        viewModel.saveCondominiumSelected("user", condoUi)
        advanceUntilIdle()

        viewModel.resetActionState()

        val state = viewModel.condominiumState.value as CondominiumState.Success
        assertFalse(state.saveSuccess)
        assertEquals(null, state.error)
    }

    @Test
    fun `saveCondominiumSelected should handle failure and update error state`() = runTest(testDispatcher) {
        val condoUi = CondominiumUiModel(id = "1", name = "Condo", cep = "123")
        
        coEvery { getUserCondominiumUseCase(any()) } returns Result.success(null)
        viewModel.fetchUserCondominium("user123")
        advanceUntilIdle()
        
        coEvery { updateUserCondominiumUseCase(any(), any()) } returns Result.failure(Exception("Network Error"))

        viewModel.saveCondominiumSelected("user123", condoUi)
        advanceUntilIdle()

        val state = viewModel.condominiumState.value as CondominiumState.Success
        assertEquals("Network Error", state.error)
        assertFalse(state.isSaving)
    }
}
