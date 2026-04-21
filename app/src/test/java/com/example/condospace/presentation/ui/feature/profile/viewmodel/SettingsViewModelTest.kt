package com.example.condospace.presentation.ui.feature.profile.viewmodel

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.example.condospace.presentation.ui.feature.profile.state.SettingsUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
class SettingsViewModelTest {

    private lateinit var updateUserUseCase: UpdateUserUseCase
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        updateUserUseCase = mockk()
        userPreferencesRepository = mockk()

        coEvery { userPreferencesRepository.userData } returns userDataFlow

        viewModel = SettingsViewModel(updateUserUseCase, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleNotifications should update state and call use case`() = runTest(testDispatcher) {
        // Arrange
        val user = UserEntity(
            uuid = "user123",
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = "",
            notificationsEnabled = true
        )
        // Initial state emission
        userDataFlow.value = user
        advanceUntilIdle()

        // Mock with delay to ensure suspension and capture intermediate state
        coEvery { userPreferencesRepository.getUserData() } coAnswers {
            delay(1000)
            user
        }
        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)

        // Act
        viewModel.toggleNotifications(false)
        
        // Assert - Check intermediate loading state
        runCurrent() // Executes until the delay() in the mock
        val currentState = viewModel.uiState.value
        assertTrue("State should be Success", currentState is SettingsUiState.Success)
        assertTrue("isUpdating should be true during the update", (currentState as SettingsUiState.Success).isUpdating)
        
        // Assert - Check final success state
        advanceUntilIdle() // Completes the coroutine
        val successState = viewModel.uiState.value as SettingsUiState.Success
        assertFalse("notificationsEnabled should be false after toggle", successState.notificationsEnabled)
        assertFalse("isUpdating should be false after completion", successState.isUpdating)
        
        coVerify { updateUserUseCase(match { it.uuid == "user123" && !it.notificationsEnabled }) }
    }

    @Test
    fun `toggleNotifications should handle failure`() = runTest(testDispatcher) {
        // Arrange
        val user = UserEntity(uuid = "user123", name = "Test", phoneNumber = "", profilePicture = null, email = "", notificationsEnabled = true)
        userDataFlow.value = user
        advanceUntilIdle()

        coEvery { userPreferencesRepository.getUserData() } returns user
        coEvery { updateUserUseCase(any()) } returns Result.failure(Exception("Error"))

        // Act
        viewModel.toggleNotifications(false)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SettingsUiState.Success
        assertEquals("Error", state.error)
        assertFalse(state.isUpdating)
    }
}
