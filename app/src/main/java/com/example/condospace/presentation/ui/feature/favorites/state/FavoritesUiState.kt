package com.example.condospace.presentation.ui.feature.favorites.state

import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState

    data class Success(
        val user: UserUiModel,
        val condominiumName: String,
        val publications: List<PublicationUiModel> = emptyList(),
        val isListLoading: Boolean = false,
        val actionError: String? = null
    ) : FavoritesUiState

    data class Error(val message: String) : FavoritesUiState
}
