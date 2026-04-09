package com.example.condospace.presentation.ui.feature.home.state

import com.example.condospace.presentation.model.ExternalServiceUiModel
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val user: UserUiModel,
        val condominiumName: String,
        val publicationsProducts: List<PublicationUiModel> = emptyList(),
        val publicationsService: List<PublicationUiModel> = emptyList(),
        val publicationsRecommendation: List<PublicationUiModel> = emptyList(),
        val externalServices: List<PublicationUiModel> = emptyList(),
        val isRefreshing: Boolean = false,
        val actionError: String? = null
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
