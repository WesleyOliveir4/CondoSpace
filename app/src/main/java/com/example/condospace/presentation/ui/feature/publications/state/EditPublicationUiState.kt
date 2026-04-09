package com.example.condospace.presentation.ui.feature.publications.state

import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

sealed interface EditPublicationUiState {
    data object Loading : EditPublicationUiState
    data class Success(
        val publication: PublicationUiModel,
        val user: UserUiModel = UserUiModel(),
        val isUpdating: Boolean = false,
        val updateSuccess: Boolean = false,
        val actionError: String? = null
    ) : EditPublicationUiState
    data class Error(val message: String) : EditPublicationUiState
}
