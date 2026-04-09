package com.example.condospace.presentation.ui.feature.publications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.EditPublicationUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toEntity
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publications.state.EditPublicationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditPublicationViewModel(
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val editPublicationUseCase: EditPublicationUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPublicationUiState>(EditPublicationUiState.Loading)
    val uiState: StateFlow<EditPublicationUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userEntity ->
                val userUiModel = userEntity?.toUiModel() ?: com.example.condospace.presentation.model.UserUiModel()
                updateState { it.copy(user = userUiModel) }
            }
        }
    }

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            _uiState.value = EditPublicationUiState.Loading
            val result = getPublicationByIdUseCase(publicationId)
            result.onSuccess { entity ->
                _uiState.value = EditPublicationUiState.Success(publication = entity.toUiModel())
            }.onFailure { exception ->
                _uiState.value = EditPublicationUiState.Error(exception.message ?: "Erro ao carregar publicação")
            }
        }
    }

    fun updatePublication(publication: PublicationUiModel) {
        viewModelScope.launch {
            updateState { it.copy(isUpdating = true, updateSuccess = false, actionError = null) }
            val result = editPublicationUseCase(publication.toEntity())
            result.onSuccess {
                updateState { it.copy(isUpdating = false, updateSuccess = true) }
            }.onFailure { exception ->
                updateState {
                    it.copy(
                        isUpdating = false,
                        actionError = exception.message ?: "Erro ao atualizar publicação"
                    )
                }
            }
        }
    }

    fun resetActionState() {
        updateState { it.copy(updateSuccess = false, actionError = null) }
    }

    private fun updateState(transform: (EditPublicationUiState.Success) -> EditPublicationUiState.Success) {
        val currentState = _uiState.value
        if (currentState is EditPublicationUiState.Success) {
            _uiState.value = transform(currentState)
        }
    }
}
