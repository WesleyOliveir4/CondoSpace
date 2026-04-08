package com.example.condospace.presentation.ui.feature.profile.state

import com.example.condospace.presentation.model.UserUiModel

sealed interface UserDataUiState {
    data object Loading : UserDataUiState
    
    data class Success(
        val user: UserUiModel,
        val isUpdating: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
    ) : UserDataUiState
    
    data class Error(val message: String) : UserDataUiState
}
