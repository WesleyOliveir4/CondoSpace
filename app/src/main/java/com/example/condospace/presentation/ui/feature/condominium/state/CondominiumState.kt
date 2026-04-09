package com.example.condospace.presentation.ui.feature.condominium.state

import com.example.condospace.presentation.model.CondominiumUiModel

sealed class CondominiumState {
    object Loading : CondominiumState()
    
    data class Success(
        val selectedCondominium: CondominiumUiModel? = null,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val error: String? = null
    ) : CondominiumState()
    
    data class Error(val message: String) : CondominiumState()
}