package com.example.condospace.presentation.ui.feature.profile.viewmodel

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.login.LogoutUseCase
import com.example.condospace.presentation.ui.feature.profile.state.ProfileUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        logoutUseCase = mockk()

        coEvery { userPreferencesRepository.userData } returns userDataFlow

        viewModel = ProfileViewModel(userPreferencesRepository, logoutUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel should load profile data when user is emitted`() = runTest {
        // Arrange
        val user = UserEntity(
            uuid = "user123",
            name = "John Doe",
            phoneNumber = "123",
            profilePicture = null,
            email = "john@test.com"
        )

        // Act
        userDataFlow.value = user
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value is ProfileUiState.Success)
        val state = viewModel.uiState.value as ProfileUiState.Success
        assertEquals("John Doe", state.user.name)
    }

    @Test
    fun `logout should call onLogoutSuccess when successful`() = runTest {
        // Arrange
        coEvery { logoutUseCase() } returns Result.success(Unit)
        var successCalled = false

        // Act
        viewModel.logout { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(successCalled)
    }
}
