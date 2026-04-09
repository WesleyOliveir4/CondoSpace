package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumAndTypeUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationsListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicationsListViewModel(
    private val getPublicationsByCondominiumAndTypeUseCase: GetPublicationsByCondominiumAndTypeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublicationsListUiState>(PublicationsListUiState.Loading)
    val uiState: StateFlow<PublicationsListUiState> = _uiState.asStateFlow()

    fun loadPublications(categoryType: String, publicationsIds: List<String>) {
        viewModelScope.launch {
            _uiState.value = PublicationsListUiState.Loading
            val result = getPublicationsByCondominiumAndTypeUseCase(categoryType, publicationsIds)
            result.onSuccess { list ->
                _uiState.value = PublicationsListUiState.Success(
                    publications = list.map { it.toUiModel() }
                )
            }.onFailure { e ->
                _uiState.value = PublicationsListUiState.Error(
                    message = e.message ?: "Erro ao carregar publicações"
                )
            }
        }
    }
}
