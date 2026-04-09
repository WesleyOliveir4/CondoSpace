package com.example.condospace.presentation.ui.feature.profile.state

sealed interface ChangePasswordUiState {
    data object Idle : ChangePasswordUiState
    data object Loading : ChangePasswordUiState
    data class Success(val message: String) : ChangePasswordUiState
    data class Error(val message: String) : ChangePasswordUiState
}
