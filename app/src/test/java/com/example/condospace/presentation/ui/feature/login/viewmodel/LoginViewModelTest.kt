package com.example.condospace.presentation.ui.feature.login.viewmodel

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.login.SignInUseCase
import com.example.condospace.presentation.ui.feature.login.state.LoginState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LoginViewModelTest {

    private lateinit var signInUseCase: SignInUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var viewModel: LoginViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signInUseCase = mockk()
        authRepository = mockk()
        userRepository = mockk()
        userPreferencesRepository = mockk()
        
        coEvery { userPreferencesRepository.getUserData() } returns null
        
        viewModel = LoginViewModel(
            signInUseCase,
            authRepository,
            userRepository,
            userPreferencesRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login should update state to Authenticated on success`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        val uid = "uid123"
        val user = mockk<UserEntity>(relaxed = true)
        
        coEvery { signInUseCase(email, password) } returns Result.success(Unit)
        coEvery { authRepository.getCurrentUserUid() } returns uid
        coEvery { userRepository.getUser(uid) } returns Result.success(user)
        coEvery { userRepository.updateUser(any()) } returns Result.success(Unit)
        coEvery { userPreferencesRepository.saveUserData(any()) } returns Unit

        // Act
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(LoginState.Authenticated, viewModel.loginState.value)
        coVerify { userPreferencesRepository.saveUserData(any()) }
    }

    @Test
    fun `login should update state to Error on failure`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        coEvery { signInUseCase(email, password) } returns Result.failure(Exception("Login error"))

        // Act
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.loginState.value is LoginState.Error)
        assertEquals("Login error", (viewModel.loginState.value as LoginState.Error).message)
    }

    @Test
    fun `login with empty fields should update state to Error`() = runTest {
        // Act
        viewModel.login("", "")

        // Assert
        assertTrue(viewModel.loginState.value is LoginState.Error)
        assertEquals("Email or password can't be empty", (viewModel.loginState.value as LoginState.Error).message)
    }
}
