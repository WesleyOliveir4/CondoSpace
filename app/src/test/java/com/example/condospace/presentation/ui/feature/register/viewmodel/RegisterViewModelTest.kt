package com.example.condospace.presentation.ui.feature.register.viewmodel

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.usecase.register.CreateUserUseCase
import com.example.condospace.domain.usecase.register.SignUpUseCase
import com.example.condospace.presentation.ui.feature.register.state.RegisterState
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
class RegisterViewModelTest {

    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        createUserUseCase = mockk()
        signUpUseCase = mockk()
        viewModel = RegisterViewModel(createUserUseCase, signUpUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signup should update state to Registered when both sign up and create user are successful`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@test.com"
        val password = "password123"
        val phone = "123456789"
        val uid = "uid123"

        coEvery { signUpUseCase(email, password) } returns Result.success(uid)
        coEvery { createUserUseCase(any()) } returns Result.success(Unit)

        // Act
        viewModel.signup(name, email, password, phone)

        // Assert
        assertTrue(viewModel.registerState.value is RegisterState.Registered)
        assertEquals(uid, (viewModel.registerState.value as RegisterState.Registered).userUuid)
    }

    @Test
    fun `signup should update state to Error when sign up fails`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@test.com"
        val password = "password123"
        val phone = "123456789"
        val errorMessage = "Email already in use"

        coEvery { signUpUseCase(email, password) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.signup(name, email, password, phone)

        // Assert
        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals(errorMessage, (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `signup should update state to Error when fields are empty`() = runTest {
        // Act
        viewModel.signup("", "", "", "")

        // Assert
        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals("All fields are required", (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `signup should update state to Error when firestore save fails`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@test.com"
        val password = "password123"
        val phone = "123456789"
        val uid = "uid123"
        val errorMessage = "Firestore error"

        coEvery { signUpUseCase(email, password) } returns Result.success(uid)
        coEvery { createUserUseCase(any()) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.signup(name, email, password, phone)

        // Assert
        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals(errorMessage, (viewModel.registerState.value as RegisterState.Error).message)
    }
}
