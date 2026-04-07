package com.example.condospace.presentation.ui.feature.condominium.state

import com.example.condospace.presentation.model.CondominiumUiModel

sealed class CondominiumState {
    object Idle : CondominiumState()
    object Loading : CondominiumState()
    data class CondominiumFound(val condominium: CondominiumUiModel) : CondominiumState()
    data class CondominiumSaved(val condominium: CondominiumUiModel) : CondominiumState()
    object CondominiumNotFound : CondominiumState()
    data class Error(val message: String) : CondominiumState()
}