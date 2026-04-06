package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationSelectedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicationSelectedViewModel(
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationSelectedUiState())
    val uiState: StateFlow<PublicationSelectedUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { user ->
                val favoriteIds = user?.publicationsIdFavored ?: emptyList()
                _uiState.update { state ->
                    state.copy(
                        isFavorite = favoriteIds.contains(state.publication?.id)
                    )
                }
            }
        }
    }

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getPublicationByIdUseCase(publicationId)
            result.onSuccess { entity ->
                val publicationUi = entity.toUiModel()
                val currentUser = userPreferencesRepository.getUserData()
                val isFavorite = currentUser?.publicationsIdFavored?.contains(publicationUi.id) == true
                
                _uiState.update { it.copy(
                    publication = publicationUi,
                    isLoading = false,
                    isFavorite = isFavorite
                ) }
            }.onFailure { exception ->
                _uiState.update { it.copy(
                    error = exception.message ?: "Erro ao carregar publicação",
                    isLoading = false
                ) }
            }
        }
    }

    fun onFavoriteClick() {
        val currentPublication = _uiState.value.publication ?: return
        val publicationId = currentPublication.id
        
        viewModelScope.launch {
            val currentUser = userPreferencesRepository.getUserData() ?: return@launch
            val currentFavorites = currentUser.publicationsIdFavored?.toMutableList() ?: mutableListOf()

            val isAdding = !currentFavorites.contains(publicationId)
            val increment = if (isAdding) 1 else -1

            if (isAdding) {
                currentFavorites.add(publicationId)
            } else {
                currentFavorites.remove(publicationId)
            }

            // 1. Update UI State immediately for better UX
            _uiState.update { state ->
                state.copy(
                    isFavorite = isAdding,
                    publication = state.publication?.copy(
                        likes = (state.publication.likes + increment).coerceAtLeast(0)
                    )
                )
            }

            val updatedUser = currentUser.copy(publicationsIdFavored = currentFavorites)

            // 2. Save to DataStore (Local)
            userPreferencesRepository.saveUserData(updatedUser)

            // 3. Save to Firestore User Table
            userRepository.updateFavoritePublications(updatedUser.uuid, currentFavorites)

            // 4. Update Likes in Publication Table
            publicationRepository.updatePublicationLikes(publicationId, increment)
        }
    }
}
