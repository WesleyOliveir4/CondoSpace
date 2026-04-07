package com.example.condospace.presentation.ui.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.login.SignInUseCase
import com.example.condospace.presentation.ui.feature.login.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Unauthenticated)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        viewModelScope.launch {
            val user = userPreferencesRepository.getUserData()
            if (user != null && user.userIsLogged) {
                _loginState.value = LoginState.Authenticated
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Email or password can't be empty")
            return
        }
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = signInUseCase(email, password)
            if (result.isSuccess) {
                saveUserDataLocally()
                _loginState.value = LoginState.Authenticated
            } else {
                _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    private suspend fun saveUserDataLocally() {
        authRepository.getCurrentUserUid()?.let { uid ->
            val userResult = userRepository.getUser(uid)
            userResult.onSuccess { user ->
                user?.let {
                    val loggedUser = it.copy(userIsLogged = true)
                    userRepository.updateUser(loggedUser)
                    userPreferencesRepository.saveUserData(loggedUser)
                }
            }
        }
    }
}
