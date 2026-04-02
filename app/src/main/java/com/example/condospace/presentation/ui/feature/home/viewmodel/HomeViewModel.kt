package com.example.condospace.presentation.ui.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val condominiumName: StateFlow<String> = userPreferencesRepository.userData
        .map { user -> user?.condominium?.name ?: "Selecionar Condomínio" }
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
}
