package com.example.condospace.presentation.ui.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.presentation.ui.feature.profile.state.UserDataUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDataUiState())
    val uiState: StateFlow<UserDataUiState> = _uiState.asStateFlow()

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            val result = authRepository.updatePassword(newPassword)
            
            result.onSuccess {
                _uiState.update { it.copy(
                    isLoading = false, 
                    successMessage = "Senha atualizada com sucesso!" 
                ) }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    isLoading = false, 
                    error = e.message ?: "Erro ao atualizar senha. Por segurança, tente fazer login novamente."
                ) }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
