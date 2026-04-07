package com.example.condospace.presentation.ui.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.example.condospace.presentation.ui.feature.profile.state.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val updateUserUseCase: UpdateUserUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collect { user ->
                _uiState.update { it.copy(notificationsEnabled = user?.notificationsEnabled ?: true) }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val currentUser = userPreferencesRepository.getUserData()
            currentUser?.let { user ->
                _uiState.update { it.copy(isLoading = true) }
                val updatedUser = user.copy(notificationsEnabled = enabled)
                
                val result = updateUserUseCase(updatedUser)
                
                result.onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao atualizar configurações"
                        ) 
                    }
                }.onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
