package com.example.condospace.presentation.ui.feature.favorites.state

import com.example.condospace.presentation.model.PublicationUiModel

data class FavoritesUiState(
    val publications: List<PublicationUiModel> = emptyList(),
    val condominiumName: String = "Carregando...",
    val userUuid: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
