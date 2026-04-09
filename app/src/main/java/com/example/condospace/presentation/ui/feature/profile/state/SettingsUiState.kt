package com.example.condospace.presentation.ui.feature.profile.state

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    
    data class Success(
        val notificationsEnabled: Boolean,
        val isUpdating: Boolean = false,
        val error: String? = null
    ) : SettingsUiState
    
    data class Error(val message: String) : SettingsUiState
}
