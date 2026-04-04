package com.example.condospace.presentation.ui.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumUseCase
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.home.state.HomeUiState
import com.example.condospace.presentation.ui.feature.publish.components.PublicationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getPublicationsByCondominiumUseCase: GetPublicationsByCondominiumUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userModel ->
                val userEntity = userModel?.toEntity()
                val uiModel = userEntity?.toUiModel() ?: UserUiModel()
                
                _uiState.update { it.copy(
                    user = uiModel,
                    condominiumName = uiModel.condominium?.name ?: "Selecionar Condomínio"
                ) }

                val condominiumId = uiModel.condominium?.id
                if (!condominiumId.isNullOrBlank()) {
                    loadServicePublications(condominiumId)
                }
            }
        }
    }

    fun loadServicePublications(condominiumId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getPublicationsByCondominiumUseCase(condominiumId)
            result.onSuccess { list ->
                _uiState.update { it.copy(
                    publications = list.filter { it.publicationType == PublicationType.SERVICE.value }.map { it.toUiModel() },
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

    fun refreshPublications() {
        val currentCondoId = _uiState.value.user.condominium?.id
        if (!currentCondoId.isNullOrBlank()) {
            loadServicePublications(currentCondoId)
        }
    }
}
