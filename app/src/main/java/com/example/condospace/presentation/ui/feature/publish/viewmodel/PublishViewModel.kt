package com.example.condospace.presentation.ui.feature.publish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.CreatePublicationUseCase
import com.example.condospace.domain.usecase.publication.DeletePublicationUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationsByUserUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.toEntity
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.publish.state.PublishUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublishViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getPublicationsByUserUseCase: GetPublicationsByUserUseCase,
    private val createPublicationUseCase: CreatePublicationUseCase,
    private val deletePublicationUseCase: DeletePublicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublishUiState>(PublishUiState.Loading)
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userEntity ->
                val uiModel = userEntity?.toUiModel() ?: com.example.condospace.presentation.model.UserUiModel()
                
                updateSuccessStateOrInitialize(uiModel)

                if (uiModel.uuid.isNotBlank()) {
                    loadMyPublications(uiModel.uuid)
                }
            }
        }
    }

    private fun updateSuccessStateOrInitialize(uiModel: com.example.condospace.presentation.model.UserUiModel) {
        val currentState = _uiState.value
        if (currentState is PublishUiState.Success) {
            _uiState.update { 
                (it as PublishUiState.Success).copy(
                    user = uiModel,
                    condominiumName = uiModel.condominium?.name ?: "Selecionar Condomínio"
                )
            }
        } else {
            _uiState.value = PublishUiState.Success(
                user = uiModel,
                condominiumName = uiModel.condominium?.name ?: "Selecionar Condomínio"
            )
        }
    }

    fun retry() {
        _uiState.value = PublishUiState.Loading
        observeUserData()
    }

    fun loadMyPublications(userId: String) {
        viewModelScope.launch {
            updateSuccessState { it.copy(isListLoading = true) }
            getPublicationsByUserUseCase(userId).collectLatest { result ->
                result.onSuccess { list ->
                    updateSuccessState { it.copy(
                        myPublications = list.map { it.toUiModel() },
                        isListLoading = false
                    ) }
                }.onFailure { e ->
                    _uiState.value = PublishUiState.Error(e.message ?: "Erro ao carregar publicações")
                }
            }
        }
    }

    fun createPublication(publication: PublicationUiModel) {
        viewModelScope.launch {
            updateSuccessState { it.copy(isPublishing = true, publishSuccess = false, actionError = null) }
            
            val result = createPublicationUseCase(publication.toEntity())
            
            result.onSuccess {
                updateSuccessState { it.copy(isPublishing = false, publishSuccess = true) }
            }.onFailure { e ->
                updateSuccessState { it.copy(
                    isPublishing = false, 
                    actionError = e.message ?: "Erro ao salvar publicação"
                ) }
            }
        }
    }

    fun deletePublication(publicationId: String) {
        viewModelScope.launch {
            updateSuccessState { it.copy(isDeleting = true, deleteSuccess = false, actionError = null) }
            
            val result = deletePublicationUseCase(publicationId)
            
            result.onSuccess {
                updateSuccessState { it.copy(isDeleting = false, deleteSuccess = true) }
            }.onFailure { e ->
                updateSuccessState { it.copy(
                    actionError = e.message ?: "Erro ao deletar publicação",
                    isDeleting = false
                ) }
            }
        }
    }

    fun resetActionState() {
        updateSuccessState { it.copy(publishSuccess = false, deleteSuccess = false, actionError = null) }
    }

    private fun updateSuccessState(transform: (PublishUiState.Success) -> PublishUiState.Success) {
        val currentState = _uiState.value
        if (currentState is PublishUiState.Success) {
            _uiState.value = transform(currentState)
        }
    }
}
