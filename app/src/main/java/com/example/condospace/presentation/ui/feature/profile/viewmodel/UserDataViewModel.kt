package com.example.condospace.presentation.ui.feature.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.user.GetUserUseCase
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.example.condospace.presentation.model.toEntity
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.ui.feature.profile.state.UserDataUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDataViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val imageRepository: ImageRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDataUiState())
    val uiState: StateFlow<UserDataUiState> = _uiState.asStateFlow()

    init {
        observeUserLocalData()
    }

    private fun observeUserLocalData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userModel ->
                userModel?.uuid?.let { userId ->
                    fetchUserFirebaseData(userId)
                }
            }
        }
    }

    private fun fetchUserFirebaseData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getUserUseCase(userId)
            result.onSuccess { userEntity ->
                userEntity?.let { entity ->
                    _uiState.update { it.copy(
                        user = entity.toUiModel(),
                        isLoading = false
                    ) }
                } ?: run {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Usuário não encontrado"
                    ) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar dados do usuário"
                ) }
            }
        }
    }

    fun updateUserName(newName: String) {
        val currentUser = _uiState.value.user
        if (currentUser.name != newName) {
            updateUser(currentUser.copy(name = newName), nameChanged = true)
        }
    }

    fun updateUserPhone(newPhone: String) {
        val currentUser = _uiState.value.user
        if (currentUser.phoneNumber != newPhone) {
            updateUser(currentUser.copy(phoneNumber = newPhone), phoneChanged = true)
        }
    }

    fun updateProfilePicture(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = _uiState.value.user.uuid
            val uploadResult = imageRepository.uploadImages(listOf(uri), "profile_pictures/$userId")
            
            uploadResult.onSuccess { images ->
                val newPhotoUrl = images.firstOrNull()?.url
                if (newPhotoUrl != null) {
                    updateUser(_uiState.value.user.copy(profilePicture = newPhotoUrl))
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Erro ao processar imagem") }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro ao fazer upload da imagem") }
            }
        }
    }

    private fun updateUser(
        updatedUser: com.example.condospace.presentation.model.UserUiModel,
        nameChanged: Boolean = false,
        phoneChanged: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = updateUserUseCase(updatedUser.toEntity())
            result.onSuccess {
                _uiState.update { it.copy(
                    user = updatedUser,
                    isLoading = false
                ) }
                if (nameChanged || phoneChanged) {
                    updateUserPublications(updatedUser, nameChanged, phoneChanged)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao atualizar dados"
                ) }
            }
        }
    }

    private fun updateUserPublications(
        user: com.example.condospace.presentation.model.UserUiModel,
        nameChanged: Boolean,
        phoneChanged: Boolean
    ) {
        viewModelScope.launch {
            publicationRepository.getPublicationsByUser(user.uuid).first().onSuccess { publications ->
                publications.forEach { publication ->
                    var updatedPublication = publication
                    
                    if (nameChanged) {
                        updatedPublication = updatedPublication.copy(publicationOwner = user.name)
                    }
                    
                    if (phoneChanged && publication.publicationType != ServiceType.RECOMMENDATION.value) {
                        updatedPublication = updatedPublication.copy(contact = user.phoneNumber)
                    }

                    if (updatedPublication != publication) {
                        publicationRepository.updatePublication(updatedPublication)
                    }
                }
            }
        }
    }
}
