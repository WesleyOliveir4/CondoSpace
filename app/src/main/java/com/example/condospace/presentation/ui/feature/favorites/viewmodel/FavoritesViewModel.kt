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

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { user ->
                _uiState.update { state ->
                    state.copy(
                        condominiumName = user?.condominiumEntity?.name ?: "Selecionar Condomínio",
                        userUuid = user?.uuid ?: ""
                    )
                }
                user?.let {
                    loadFavorites(it.uuid, it.publicationsIdFavored ?: emptyList())
                }
            }
        }
    }

    private fun loadFavorites(userId: String, favoriteIds: List<String>) {
        if (favoriteIds.isEmpty()) {
            _uiState.update { it.copy(publications = emptyList(), isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getFavoritePublicationsUseCase(userId, favoriteIds)
            
            result.onSuccess { entities ->
                _uiState.update { state ->
                    state.copy(
                        publications = entities.map { it.toUiModel() },
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(
                    error = error.message ?: "Erro ao carregar favoritos",
                    isLoading = false
                ) }
            }
        }
    }
}
