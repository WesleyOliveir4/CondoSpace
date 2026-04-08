package com.example.condospace.presentation.ui.feature.publish.state

import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

sealed interface PublishUiState {
    data object Loading : PublishUiState

    data class Success(
        val user: UserUiModel,
        val condominiumName: String,
        val myPublications: List<PublicationUiModel> = emptyList(),
        val isListLoading: Boolean = false,
        val isPublishing: Boolean = false,
        val actionError: String? = null,
        val publishSuccess: Boolean = false
    ) : PublishUiState

    data class Error(val message: String) : PublishUiState
}
