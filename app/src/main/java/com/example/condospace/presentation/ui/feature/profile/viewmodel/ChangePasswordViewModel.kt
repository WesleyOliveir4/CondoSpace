package com.example.condospace.presentation.ui.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.presentation.ui.feature.profile.state.ChangePasswordUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading
            val result = authRepository.updatePassword(newPassword)
            
            result.onSuccess {
                _uiState.value = ChangePasswordUiState.Success("Senha atualizada com sucesso!")
            }.onFailure { e ->
                _uiState.value = ChangePasswordUiState.Error(
                    e.message ?: "Erro ao atualizar senha. Por segurança, tente fazer login novamente."
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ChangePasswordUiState.Idle
    }
}
