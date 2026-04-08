package com.example.condospace.presentation.ui.feature.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.GetFavoritePublicationsUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.favorites.state.FavoritesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getFavoritePublicationsUseCase: GetFavoritePublicationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userEntity ->
                if (userEntity == null) {
                    _uiState.value = FavoritesUiState.Error("Usuário não encontrado")
                    return@collectLatest
                }

                val userUi = userEntity.toUiModel()
                val condominiumName = userUi.condominium?.name ?: "Selecionar Condomínio"
                val favoriteIds = userUi.publicationsIdFavored ?: emptyList()

                _uiState.update { currentState ->
                    if (currentState is FavoritesUiState.Success) {
                        currentState.copy(
                            user = userUi,
                            condominiumName = condominiumName,
                            isListLoading = true
                        )
                    } else {
                        FavoritesUiState.Success(
                            user = userUi,
                            condominiumName = condominiumName,
                            isListLoading = true
                        )
                    }
                }

                loadFavorites(userUi.uuid, favoriteIds)
            }
        }
    }

    private fun loadFavorites(userId: String, favoriteIds: List<String>) {
        if (favoriteIds.isEmpty()) {
            _uiState.update { currentState ->
                if (currentState is FavoritesUiState.Success) {
                    currentState.copy(
                        publications = emptyList(),
                        isListLoading = false,
                        actionError = null
                    )
                } else {
                    _uiState.value = FavoritesUiState.Error("Estado inesperado")
                    currentState
                }
            }
            return
        }

        viewModelScope.launch {
            val result = getFavoritePublicationsUseCase(userId, favoriteIds)
            
            result.onSuccess { entities ->
                val publications = entities.map { it.toUiModel() }
                _uiState.update { currentState ->
                    if (currentState is FavoritesUiState.Success) {
                        currentState.copy(
                            publications = publications,
                            isListLoading = false,
                            actionError = null
                        )
                    } else {
                        currentState
                    }
                }
            }.onFailure { error ->
                _uiState.update { currentState ->
                    if (currentState is FavoritesUiState.Success) {
                        currentState.copy(
                            isListLoading = false,
                            actionError = error.message ?: "Erro ao carregar favoritos"
                        )
                    } else {
                        FavoritesUiState.Error(error.message ?: "Erro ao carregar favoritos")
                    }
                }
            }
        }
    }
}
