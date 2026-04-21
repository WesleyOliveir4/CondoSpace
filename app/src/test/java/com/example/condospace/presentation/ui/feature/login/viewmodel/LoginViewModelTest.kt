package com.example.condospace.presentation.ui.feature.login.viewmodel

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.login.SignInUseCase
import com.example.condospace.presentation.ui.feature.login.state.LoginState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class LoginViewModelTest {

    private lateinit var signInUseCase: SignInUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signInUseCase = mockk()
        authRepository = mockk()
        userRepository = mockk()
        userPreferencesRepository = mockk(relaxed = true)
        
        coEvery { userPreferencesRepository.getUserData() } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should set state to Authenticated when user is already logged in`() = runTest {
        // Arrange
        val user = mockk<UserEntity>()
        every { user.userIsLogged } returns true
        coEvery { userPreferencesRepository.getUserData() } returns user

        // Act
        viewModel = LoginViewModel(signInUseCase, authRepository, userRepository, userPreferencesRepository)

        // Assert
        assertEquals(LoginState.Authenticated, viewModel.loginState.value)
    }

    @Test
    fun `login should update state to Authenticated when successful`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        val uid = "uid123"
        val user = UserEntity(
            uuid = uid,
            name = "Test",
            phoneNumber = "",
            profilePicture = null,
            email = email,
            userIsLogged = false
        )

        viewModel = LoginViewModel(signInUseCase, authRepository, userRepository, userPreferencesRepository)
        coEvery { signInUseCase(email, password) } returns Result.success(Unit)
        every { authRepository.getCurrentUserUid() } returns uid
        coEvery { userRepository.getUser(uid) } returns Result.success(user)
        coEvery { userRepository.updateUser(any()) } returns Result.success(Unit)

        // Act
        viewModel.login(email, password)

        // Assert
        assertEquals(LoginState.Authenticated, viewModel.loginState.value)
        coVerify { userPreferencesRepository.saveUserData(any()) }
    }

    @Test
    fun `login should update state to Error when sign in fails`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password"
        val errorMessage = "Invalid credentials"
        
        viewModel = LoginViewModel(signInUseCase, authRepository, userRepository, userPreferencesRepository)
        coEvery { signInUseCase(email, password) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.login(email, password)

        // Assert
        assertTrue(viewModel.loginState.value is LoginState.Error)
        assertEquals(errorMessage, (viewModel.loginState.value as LoginState.Error).message)
    }

    @Test
    fun `login should update state to Error when email or password is empty`() = runTest {
        // Arrange
        viewModel = LoginViewModel(signInUseCase, authRepository, userRepository, userPreferencesRepository)

        // Act
        viewModel.login("", "")

        // Assert
        assertTrue(viewModel.loginState.value is LoginState.Error)
        assertEquals("Email or password can't be empty", (viewModel.loginState.value as LoginState.Error).message)
    }
}
