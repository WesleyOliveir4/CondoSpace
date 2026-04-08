package com.example.condospace.presentation.ui.feature.profile.state

import com.example.condospace.presentation.model.UserUiModel

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    
    data class Success(
        val user: UserUiModel,
        val condominiumName: String,
        val error: String? = null
    ) : ProfileUiState
    
    data class Error(val message: String) : ProfileUiState
}
