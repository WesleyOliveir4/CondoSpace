package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EditPublicationState {
    object Loading : EditPublicationState()
    data class Success(val publication: PublicationUiModel) : EditPublicationState()
    data class Error(val message: String) : EditPublicationState()
}

class EditPublicationViewModel(
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPublicationState>(EditPublicationState.Loading)
    val uiState: StateFlow<EditPublicationState> = _uiState.asStateFlow()

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            _uiState.value = EditPublicationState.Loading
            val result = getPublicationByIdUseCase(publicationId)
            result.onSuccess { entity ->
                _uiState.value = EditPublicationState.Success(entity.toUiModel())
            }.onFailure { exception ->
                _uiState.value = EditPublicationState.Error(exception.message ?: "Erro ao carregar publicação")
            }
        }
    }
}
