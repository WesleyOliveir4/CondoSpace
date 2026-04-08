package com.example.condospace.presentation.ui.feature.publications.state

import com.example.condospace.presentation.model.PublicationUiModel

sealed interface PublicationSelectedUiState {
    data object Loading : PublicationSelectedUiState
    data class Success(
        val publication: PublicationUiModel,
        val isFavorite: Boolean = false,
        val actionError: String? = null
    ) : PublicationSelectedUiState
    data class Error(val message: String) : PublicationSelectedUiState
}
