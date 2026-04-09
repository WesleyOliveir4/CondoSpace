package com.example.condospace.presentation.ui.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumUseCase
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.ui.feature.home.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getPublicationsByCondominiumUseCase: GetPublicationsByCondominiumUseCase,
    private val externalServiceRepository: ExternalServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userEntity ->
                if (userEntity == null) {
                    _uiState.value = HomeUiState.Error("Usuário não encontrado")
                } else {
                    handleUserUpdate(userEntity.toUiModel())
                }
            }
        }
    }

    private fun handleUserUpdate(userUi: UserUiModel) {
        val condominiumId = userUi.condominium?.id
        val cep = userUi.condominium?.cep
        
        updateSuccessOrInit(userUi)

        if (!condominiumId.isNullOrBlank()) {
            fetchHomeContent(condominiumId, cep)
        } else {
            clearHomeData()
        }
    }

    private fun fetchHomeContent(condominiumId: String, cep: String?) {
        viewModelScope.launch {
            updateSuccess { it.copy(isRefreshing = true) }
            
            val publicationsTask = launch {
                getPublicationsByCondominiumUseCase(condominiumId)
                    .onSuccess { list -> updateHomeData(list) }
                    .onFailure { e -> handleFailure(e) }
            }

            val externalServicesTask = launch {
                if (!cep.isNullOrBlank()) {
                    externalServiceRepository.getNearbyServices(cep)
                        .onSuccess { services -> 
                            updateSuccess { it.copy(externalServices = services.map { externalServicesEntity -> externalServicesEntity.toUiModel() }) }
                        }
                        .onFailure { e -> handleFailure(e) }
                }
            }
        }
    }

    private fun updateHomeData(list: List<PublicationEntity>) {
        val services = list.filter { it.publicationType == ServiceType.SERVICE.value }.map { it.toUiModel() }
        val recommendations = list.filter { it.publicationType == ServiceType.RECOMMENDATION.value }.map { it.toUiModel() }

        updateSuccess { 
            it.copy(
                publicationsService = services,
                publicationsRecommendation = recommendations,
                isRefreshing = false,
                actionError = null
            )
        }
    }

    private fun handleFailure(e: Throwable) {
        val message = e.message ?: "Erro ao carregar dados"
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) {
                currentState.copy(isRefreshing = false, actionError = message)
            } else {
                HomeUiState.Error(message)
            }
        }
    }

    private fun clearHomeData() {
        updateSuccess { 
            it.copy(
                publicationsService = emptyList(), 
                publicationsRecommendation = emptyList(),
                externalServices = emptyList(),
                isRefreshing = false 
            ) 
        }
    }

    private fun updateSuccessOrInit(userUi: UserUiModel) {
        val condominiumName = userUi.condominium?.name ?: "Selecionar Condomínio"
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) {
                currentState.copy(user = userUi, condominiumName = condominiumName)
            } else {
                HomeUiState.Success(user = userUi, condominiumName = condominiumName)
            }
        }
    }

    private fun updateSuccess(block: (HomeUiState.Success) -> HomeUiState.Success) {
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) block(currentState) else currentState
        }
    }

    fun refreshPublications() {
        val user = (uiState.value as? HomeUiState.Success)?.user
        user?.condominium?.id?.let { 
            fetchHomeContent(it, user.condominium.cep)
        }
    }

    fun resetActionError() = updateSuccess { it.copy(actionError = null) }

    fun retry() {
        _uiState.value = HomeUiState.Loading
        observeUserData()
    }
}
