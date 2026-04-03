package com.example.condospace.presentation.ui.feature.publish.state

import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

data class PublishUiState(
    val user: UserUiModel = UserUiModel(),
    val condominiumName: String = "Carregando...",
    val myPublications: List<PublicationUiModel> = emptyList(),
    val isListLoading: Boolean = false,
    val isPublishing: Boolean = false,
    val error: String? = null,
    val publishSuccess: Boolean = false
)
