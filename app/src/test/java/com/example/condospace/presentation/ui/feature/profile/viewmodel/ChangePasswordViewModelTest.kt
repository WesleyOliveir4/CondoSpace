package com.example.condospace.presentation.ui.feature.profile.viewmodel

import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.presentation.ui.feature.profile.state.ChangePasswordUiState
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
class ChangePasswordViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: ChangePasswordViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = ChangePasswordViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updatePassword should update state to Success when repository returns success`() = runTest {
        // Arrange
        val newPassword = "newPassword123"
        coEvery { authRepository.updatePassword(newPassword) } returns Result.success(Unit)

        // Act
        viewModel.updatePassword(newPassword)

        // Assert
        assertTrue(viewModel.uiState.value is ChangePasswordUiState.Success)
        assertEquals("Senha atualizada com sucesso!", (viewModel.uiState.value as ChangePasswordUiState.Success).message)
    }

    @Test
    fun `updatePassword should update state to Error when repository returns failure`() = runTest {
        // Arrange
        val newPassword = "newPassword123"
        val errorMessage = "Update failed"
        coEvery { authRepository.updatePassword(newPassword) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.updatePassword(newPassword)

        // Assert
        assertTrue(viewModel.uiState.value is ChangePasswordUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as ChangePasswordUiState.Error).message)
    }

    @Test
    fun `resetState should set state to Idle`() {
        // Arrange
        val newPassword = "newPassword123"
        coEvery { authRepository.updatePassword(newPassword) } returns Result.success(Unit)
        viewModel.updatePassword(newPassword)

        // Act
        viewModel.resetState()

        // Assert
        assertEquals(ChangePasswordUiState.Idle, viewModel.uiState.value)
    }
}
