package com.example.condospace.presentation.ui.feature.register.viewmodel

import com.example.condospace.domain.usecase.register.CreateUserUseCase
import com.example.condospace.domain.usecase.register.SignUpUseCase
import com.example.condospace.presentation.ui.feature.register.state.RegisterState
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        createUserUseCase = mockk()
        signUpUseCase = mockk()
        
        // Generic stubs to avoid "no answer found" in tests that don't focus on these calls
        coEvery { createUserUseCase(any()) } returns Result.success(Unit)
        coEvery { signUpUseCase(any(), any()) } returns Result.success("default_uid")

        mockkStatic("com.example.condospace.presentation.model.UserUiModelKt")
        viewModel = RegisterViewModel(createUserUseCase, signUpUseCase)
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.UserUiModelKt")
        Dispatchers.resetMain()
    }

    @Test
    fun `signup should update state to Registered on success`() = runTest {
        val uid = "user123"
        coEvery { signUpUseCase(any(), any()) } returns Result.success(uid)
        coEvery { createUserUseCase(any()) } returns Result.success(Unit)

        viewModel.signup("Name", "email@test.com", "password", "123456")
        advanceUntilIdle()

        assertTrue(viewModel.registerState.value is RegisterState.Registered)
        assertEquals(uid, (viewModel.registerState.value as RegisterState.Registered).userUuid)
    }

    @Test
    fun `signup should update state to Error when fields are empty`() = runTest {
        viewModel.signup("", "", "", "")

        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals("All fields are required", (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `signup should update state to Error when signUpUseCase fails`() = runTest {
        val errorMessage = "Email already in use"
        coEvery { signUpUseCase(any(), any()) } returns Result.failure(Exception(errorMessage))

        viewModel.signup("Name", "email@test.com", "password", "123456")
        advanceUntilIdle()

        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals(errorMessage, (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `signup should update state to Error with default message when signUpUseCase fails without message`() = runTest {
        coEvery { signUpUseCase(any(), any()) } returns Result.failure(Exception())

        viewModel.signup("Name", "email@test.com", "password", "123456")
        advanceUntilIdle()

        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals("SignUp failed", (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `signup should update state to Error when createUserUseCase fails`() = runTest {
        val uid = "user123"
        val errorMessage = "Firestore error"
        coEvery { signUpUseCase(any(), any()) } returns Result.success(uid)
        coEvery { createUserUseCase(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.signup("Name", "email@test.com", "password", "123456")
        advanceUntilIdle()

        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals(errorMessage, (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `signup should update state to Error with default message when createUserUseCase fails without message`() = runTest {
        val uid = "user123"
        coEvery { signUpUseCase(any(), any()) } returns Result.success(uid)
        coEvery { createUserUseCase(any()) } returns Result.failure(Exception())

        viewModel.signup("Name", "email@test.com", "password", "123456")
        advanceUntilIdle()

        assertTrue(viewModel.registerState.value is RegisterState.Error)
        assertEquals("Failed to save user data", (viewModel.registerState.value as RegisterState.Error).message)
    }

    @Test
    fun `dismissError should reset state to NotRegistered`() = runTest {
        // First set an error state
        viewModel.signup("", "", "", "")
        assertTrue(viewModel.registerState.value is RegisterState.Error)

        viewModel.dismissError()
        runCurrent()

        assertEquals(RegisterState.NotRegistered, viewModel.registerState.value)
    }

    @Test
    fun `signup should show loading state while processing`() = runTest {
        coEvery { signUpUseCase(any(), any()) } coAnswers {
            // Delay to allow checking state
            kotlinx.coroutines.delay(100)
            Result.success("uid")
        }

        viewModel.signup("Name", "email@test.com", "password", "123456")
        
        // Use runCurrent to reach the first suspension point (signUpUseCase)
        runCurrent()
        assertEquals(RegisterState.Loading, viewModel.registerState.value)
        
        advanceUntilIdle()
    }
}
