package com.example.condospace.presentation.ui.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.profile.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collectLatest { userModel ->
                val userEntity = userModel?.toEntity()
                val uiModel = userEntity?.toUiModel() ?: UserUiModel()

                _uiState.update { it.copy(
                    user = uiModel,
                    condominiumName = uiModel.condominium?.name ?: "Selecionar Condomínio"
                ) }
            }
        }
    }
}
