package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumAndTypeUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationsListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicationsListViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getPublicationsByCondominiumAndTypeUseCase: GetPublicationsByCondominiumAndTypeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationsListUiState())
    val uiState: StateFlow<PublicationsListUiState> = _uiState.asStateFlow()

    private var currentCategory: String? = null

    fun init(categoryType: String) {
        currentCategory = categoryType
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userModel ->
                val userEntity = userModel?.toEntity()
                val condominiumId = userEntity?.condominiumEntity?.id
                
                if (!condominiumId.isNullOrBlank()) {
                    loadPublications(condominiumId, currentCategory)
                }
            }
        }
    }

    private fun loadPublications(condominiumId: String, type: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getPublicationsByCondominiumAndTypeUseCase(condominiumId, type)
            result.onSuccess { list ->
                _uiState.update { it.copy(
                    publications = list.map { it.toUiModel() },
                    isLoading = false
                ) }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    error = e.message ?: "Erro ao carregar publicações",
                    isLoading = false
                ) }
            }
        }
    }
}
