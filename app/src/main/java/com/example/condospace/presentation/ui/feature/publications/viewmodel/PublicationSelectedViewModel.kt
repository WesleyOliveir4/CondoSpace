package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationSelectedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicationSelectedViewModel(
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationSelectedUiState())
    val uiState: StateFlow<PublicationSelectedUiState> = _uiState.asStateFlow()

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getPublicationByIdUseCase(publicationId)
            result.onSuccess { entity ->
                _uiState.update { it.copy(
                    publication = entity.toUiModel(),
                    isLoading = false
                ) }
            }.onFailure { exception ->
                _uiState.update { it.copy(
                    error = exception.message ?: "Erro ao carregar publicação",
                    isLoading = false
                ) }
            }
        }
    }
}
