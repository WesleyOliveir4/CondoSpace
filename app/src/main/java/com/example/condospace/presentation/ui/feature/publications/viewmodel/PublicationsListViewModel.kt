package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumAndTypeUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationsListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PublicationsListViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getPublicationsByCondominiumAndTypeUseCase: GetPublicationsByCondominiumAndTypeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublicationsListUiState>(PublicationsListUiState.Loading)
    val uiState: StateFlow<PublicationsListUiState> = _uiState.asStateFlow()

    private var currentCategory: String? = null

    fun init(categoryType: String) {
        currentCategory = categoryType
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userEntity ->
                val condominiumId = userEntity?.condominiumEntity?.id
                
                if (!condominiumId.isNullOrBlank()) {
                    loadPublications(condominiumId, currentCategory)
                }
            }
        }
    }

    private fun loadPublications(condominiumId: String, type: String?) {
        viewModelScope.launch {
            _uiState.value = PublicationsListUiState.Loading
            val result = getPublicationsByCondominiumAndTypeUseCase(condominiumId, type)
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
