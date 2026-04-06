package com.example.condospace.presentation.ui.feature.profile.state

import com.example.condospace.presentation.model.UserUiModel

data class UserDataUiState(
    val user: UserUiModel = UserUiModel(),
    val isLoading: Boolean = false,
    val error: String? = null
)
