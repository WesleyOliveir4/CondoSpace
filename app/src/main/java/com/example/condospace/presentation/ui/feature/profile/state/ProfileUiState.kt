package com.example.condospace.presentation.ui.feature.profile.state

import com.example.condospace.presentation.model.UserUiModel

data class ProfileUiState(
    val user: UserUiModel = UserUiModel(),
    val condominiumName: String = "Selecionar Condomínio",
    val isLoading: Boolean = false,
    val error: String? = null
)
