package com.example.condospace.presentation.ui.feature.publish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
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
    private val publicationRepository: PublicationRepository,
    private val imageRepository: ImageRepository,
    private val getPublicationsByUserUseCase: GetPublicationsByUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userModel ->
                val userEntity = userModel?.toEntity()
                val uiModel = userEntity?.toUiModel() ?: com.example.condospace.presentation.model.UserUiModel()
                
                _uiState.update { it.copy(
                    user = uiModel,
                    condominiumName = uiModel.condominium?.name ?: "Selecionar Condomínio"
                ) }

                if (uiModel.uuid.isNotBlank()) {
                    loadMyPublications(uiModel.uuid)
                }
            }
        }
    }

    fun loadMyPublications(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isListLoading = true) }
            val result = getPublicationsByUserUseCase(userId)
            result.onSuccess { list ->
                _uiState.update { it.copy(
                    myPublications = list.map { it.toUiModel() },
                    isListLoading = false
                ) }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    error = e.message ?: "Erro ao carregar publicações",
                    isListLoading = false
                ) }
            }
        }
    }

    fun createPublication(publication: PublicationUiModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true, publishSuccess = false, error = null) }
            try {
                val uris = publication.imagesSelectList ?: emptyList()
                val uploadedImages = if (uris.isNotEmpty() && uris.any { !it.toString().startsWith("http") }) {
                    val result = imageRepository.uploadImages(uris, "publications/${publication.id}")
                    if (result.isSuccess) {
                        result.getOrDefault(emptyList())
                    } else {
                        _uiState.update { it.copy(
                            isPublishing = false, 
                            error = result.exceptionOrNull()?.message ?: "Erro no upload das imagens"
                        ) }
                        return@launch
                    }
                } else {
                    emptyList()
                }

                val finalPublication = if (uploadedImages.isNotEmpty()) {
                    publication.copy(imageUrlList = uploadedImages.map { it.toUiModel() })
                } else {
                    publication
                }
                
                val saveResult = publicationRepository.createPublication(finalPublication.toEntity())
                
                if (saveResult.isSuccess) {
                    _uiState.update { it.copy(isPublishing = false, publishSuccess = true) }
                    loadMyPublications(_uiState.value.user.uuid)
                } else {
                    _uiState.update { it.copy(
                        isPublishing = false, 
                        error = saveResult.exceptionOrNull()?.message ?: "Erro ao salvar publicação"
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isPublishing = false, error = e.message ?: "Erro desconhecido") }
            }
        }
    }

    fun resetActionState() {
        _uiState.update { it.copy(publishSuccess = false, error = null) }
    }
}
