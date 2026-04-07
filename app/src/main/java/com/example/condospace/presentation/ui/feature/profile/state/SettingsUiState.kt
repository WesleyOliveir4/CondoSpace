package com.example.condospace.presentation.ui.feature.profile.state

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)
