package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.publication.GetContactUrlUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.PublicationSelectedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PublicationSelectedViewModel(
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository,
    private val getContactUrlUseCase: GetContactUrlUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublicationSelectedUiState>(PublicationSelectedUiState.Loading)
    val uiState: StateFlow<PublicationSelectedUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { user ->
                val favoriteIds = user?.publicationsIdFavored ?: emptyList()
                val currentState = _uiState.value
                if (currentState is PublicationSelectedUiState.Success) {
                    _uiState.value = currentState.copy(
                        isFavorite = favoriteIds.contains(currentState.publication.id)
                    )
                }
            }
        }
    }

    fun loadPublication(publicationId: String, category: String?) {
        viewModelScope.launch {
            _uiState.value = PublicationSelectedUiState.Loading
            val result = getPublicationByIdUseCase(publicationId, category)
            result.onSuccess { entity ->
                val publicationUi = entity.toUiModel()
                val currentUser = userPreferencesRepository.getUserData()
                val isFavorite = currentUser?.publicationsIdFavored?.contains(publicationUi.id) == true
                
                _uiState.value = PublicationSelectedUiState.Success(
                    publication = publicationUi,
                    isFavorite = isFavorite
                )
            }.onFailure { exception ->
                _uiState.value = PublicationSelectedUiState.Error(
                    message = exception.message ?: "Erro ao carregar publicação"
                )
            }
        }
    }

    fun onFavoriteClick() {
        val currentState = _uiState.value
        if (currentState !is PublicationSelectedUiState.Success) return
        
        val publicationId = currentState.publication.id
        
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
            _uiState.value = currentState.copy(
                isFavorite = isAdding,
                publication = currentState.publication.copy(
                    likes = (currentState.publication.likes + increment).coerceAtLeast(0)
                )
            )

            val updatedUser = currentUser.copy(publicationsIdFavored = currentFavorites)

            // 2. Save to DataStore (Local)
            userPreferencesRepository.saveUserData(updatedUser)

            // 3. Save to Firestore User Table
            userRepository.updateFavoritePublications(updatedUser.uuid, currentFavorites)

            // 4. Update Likes in Publication Table
            publicationRepository.updatePublicationLikes(publicationId, increment)
        }
    }

    fun getContactUrl(publication: PublicationUiModel, categoryType: String?): String? {
        return getContactUrlUseCase(
            contact = publication.contact,
            publicationOwner = publication.publicationOwner,
            title = publication.title,
            categoryType = categoryType
        )
    }

    fun resetActionError() {
        val currentState = _uiState.value
        if (currentState is PublicationSelectedUiState.Success) {
            _uiState.value = currentState.copy(actionError = null)
        }
    }
}
