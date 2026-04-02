package com.example.condospace.presentation.ui.feature.publish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.model.Publication
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class PublishState {
    object Idle : PublishState()
    object Loading : PublishState()
    object Success : PublishState()
    data class Error(val message: String) : PublishState()
}

class PublishViewModel(
    userPreferencesRepository: UserPreferencesRepository,
    private val publicationRepository: PublicationRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _publishState = MutableStateFlow<PublishState>(PublishState.Idle)
    val publishState: StateFlow<PublishState> = _publishState.asStateFlow()

    val condominiumName: StateFlow<String> = userPreferencesRepository.userData
        .map { user -> user?.condominiumName ?: "Selecionar Condomínio" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Carregando..."
        )

    val userUuid: StateFlow<String> = userPreferencesRepository.userData
        .map { user -> user?.uuid ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun createPublication(publication: Publication) {
        _publishState.value = PublishState.Loading
        viewModelScope.launch {
            try {
                val uris = publication.imagesSelectList ?: emptyList()
                val uploadedImages = if (uris.isNotEmpty()) {
                    val result = imageRepository.uploadImages(uris, "publications/${publication.id}")
                    if (result.isSuccess) {
                        result.getOrDefault(emptyList())
                    } else {
                        _publishState.value = PublishState.Error(result.exceptionOrNull()?.message ?: "Erro no upload das imagens")
                        return@launch
                    }
                } else {
                    emptyList()
                }

                val finalPublication = publication.copy(imageUrlList = uploadedImages)
                val saveResult = publicationRepository.createPublication(finalPublication)
                
                if (saveResult.isSuccess) {
                    _publishState.value = PublishState.Success
                } else {
                    _publishState.value = PublishState.Error(saveResult.exceptionOrNull()?.message ?: "Erro ao salvar publicação")
                }
            } catch (e: Exception) {
                _publishState.value = PublishState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}
