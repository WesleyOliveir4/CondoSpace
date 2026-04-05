package com.example.condospace.presentation.ui.feature.publications.state

import com.example.condospace.presentation.model.PublicationUiModel

data class PublicationsListUiState(
    val publications: List<PublicationUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
