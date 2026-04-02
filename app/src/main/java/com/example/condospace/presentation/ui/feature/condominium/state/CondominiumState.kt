package com.example.condospace.presentation.ui.feature.condominium.state

import com.example.condospace.domain.model.Condominium

sealed class CondominiumState {
    object Loading : CondominiumState()
    data class CondominiumFound(val condominium: Condominium) : CondominiumState()
    data class CondominiumSaved(val condominium: Condominium) : CondominiumState()
    object CondominiumNotFound : CondominiumState()
    data class Error(val message: String) : CondominiumState()
}