package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.EditPublicationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPublicationViewModel(
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPublicationUiState>(EditPublicationUiState.Loading)
    val uiState: StateFlow<EditPublicationUiState> = _uiState.asStateFlow()

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            _uiState.value = EditPublicationUiState.Loading
            val result = getPublicationByIdUseCase(publicationId)
            result.onSuccess { entity ->
                _uiState.value = EditPublicationUiState.Success(entity.toUiModel())
            }.onFailure { exception ->
                _uiState.value = EditPublicationUiState.Error(exception.message ?: "Erro ao carregar publicação")
            }
        }
    }
}
