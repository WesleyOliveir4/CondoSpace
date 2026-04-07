package com.example.condospace.presentation.ui.feature.login.state

sealed class LoginState {
    object Checking : LoginState()
    object Authenticated : LoginState()
    object Unauthenticated : LoginState()
    object Loading : LoginState()
    data class Error(val message: String) : LoginState()
}
