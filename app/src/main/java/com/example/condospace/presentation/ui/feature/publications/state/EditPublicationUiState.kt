package com.example.condospace.presentation.ui.feature.publications.state

import com.example.condospace.presentation.model.PublicationUiModel

sealed interface EditPublicationUiState {
    data object Loading : EditPublicationUiState
    data class Success(val publication: PublicationUiModel) : EditPublicationUiState
    data class Error(val message: String) : EditPublicationUiState
}
