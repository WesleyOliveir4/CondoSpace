package com.example.condospace.presentation.ui.feature.home.state

import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

data class HomeUiState(
    val user: UserUiModel = UserUiModel(),
    val condominiumName: String = "Selecionar Condomínio",
    val publications: List<PublicationUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
