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
import kotlinx.coroutines.launch

class UserDataViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val imageRepository: ImageRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDataUiState>(UserDataUiState.Loading)
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
            _uiState.value = UserDataUiState.Loading
            val result = getUserUseCase(userId)
            result.onSuccess { userEntity ->
                userEntity?.let { entity ->
                    _uiState.value = UserDataUiState.Success(
                        user = entity.toUiModel()
                    )
                } ?: run {
                    _uiState.value = UserDataUiState.Error("Usuário não encontrado")
                }
            }.onFailure { e ->
                _uiState.value = UserDataUiState.Error(
                    e.message ?: "Erro ao carregar dados do usuário"
                )
            }
        }
    }

    fun updateUserName(newName: String) {
        val currentState = _uiState.value
        if (currentState is UserDataUiState.Success) {
            if (currentState.user.name != newName) {
                updateUser(currentState.user.copy(name = newName), nameChanged = true)
            }
        }
    }

    fun updateUserPhone(newPhone: String) {
        val currentState = _uiState.value
        if (currentState is UserDataUiState.Success) {
            if (currentState.user.phoneNumber != newPhone) {
                updateUser(currentState.user.copy(phoneNumber = newPhone), phoneChanged = true)
            }
        }
    }

    fun updateProfilePicture(uri: Uri) {
        val currentState = _uiState.value
        if (currentState is UserDataUiState.Success) {
            viewModelScope.launch {
                _uiState.value = currentState.copy(isUpdating = true)
                val userId = currentState.user.uuid
                val uploadResult = imageRepository.uploadImages(listOf(uri), "profile_pictures/$userId")
                
                uploadResult.onSuccess { images ->
                    val newPhotoUrl = images.firstOrNull()?.url
                    if (newPhotoUrl != null) {
                        updateUser(currentState.user.copy(profilePicture = newPhotoUrl))
                    } else {
                        _uiState.value = currentState.copy(
                            isUpdating = false, 
                            error = "Erro ao processar imagem"
                        )
                    }
                }.onFailure { e ->
                    _uiState.value = currentState.copy(
                        isUpdating = false, 
                        error = e.message ?: "Erro ao fazer upload da imagem"
                    )
                }
            }
        }
    }

    private fun updateUser(
        updatedUser: com.example.condospace.presentation.model.UserUiModel,
        nameChanged: Boolean = false,
        phoneChanged: Boolean = false
    ) {
        val currentState = _uiState.value
        if (currentState is UserDataUiState.Success) {
            _uiState.value = currentState.copy(isUpdating = true)
            viewModelScope.launch {
                val result = updateUserUseCase(updatedUser.toEntity())
                result.onSuccess {
                    _uiState.value = currentState.copy(
                        user = updatedUser,
                        isUpdating = false,
                        successMessage = "Dados atualizados com sucesso"
                    )
                    if (nameChanged || phoneChanged) {
                        updateUserPublications(updatedUser, nameChanged, phoneChanged)
                    }
                }.onFailure { e ->
                    _uiState.value = currentState.copy(
                        isUpdating = false,
                        error = e.message ?: "Erro ao atualizar dados"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        val currentState = _uiState.value
        if (currentState is UserDataUiState.Success) {
            _uiState.value = currentState.copy(error = null, successMessage = null)
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
