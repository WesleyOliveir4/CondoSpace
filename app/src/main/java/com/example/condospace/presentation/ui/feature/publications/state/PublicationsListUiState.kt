package com.example.condospace.presentation.ui.feature.publications.state

import com.example.condospace.presentation.model.PublicationUiModel

sealed interface PublicationsListUiState {
    data object Loading : PublicationsListUiState
    data class Success(val publications: List<PublicationUiModel>) : PublicationsListUiState
    data class Error(val message: String) : PublicationsListUiState
}
