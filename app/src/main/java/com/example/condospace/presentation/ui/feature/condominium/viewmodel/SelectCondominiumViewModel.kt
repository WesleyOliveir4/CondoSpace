package com.example.condospace.presentation.ui.feature.condominium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.model.Condominium
import com.example.condospace.domain.usecase.GetUserCondominiumUseCase
import com.example.condospace.domain.usecase.SaveCondominiumUseCase
import com.example.condospace.domain.usecase.SearchCondominiumByCepUseCase
import com.example.condospace.domain.usecase.UpdateUserCondominiumUseCase
import com.example.condospace.presentation.ui.feature.condominium.state.CondominiumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SelectCondominiumViewModel(
    private val getUserCondominiumUseCase: GetUserCondominiumUseCase,
    private val searchCondominiumByCepUseCase: SearchCondominiumByCepUseCase,
    private val saveCondominiumUseCase: SaveCondominiumUseCase,
    private val updateUserCondominiumUseCase: UpdateUserCondominiumUseCase
) : ViewModel() {

    private val _condominiumState = MutableStateFlow<CondominiumState>(CondominiumState.Loading)
    val condominiumState: StateFlow<CondominiumState> = _condominiumState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Condominium>>(emptyList())
    val searchResults: StateFlow<List<Condominium>> = _searchResults.asStateFlow()

    fun fetchUserCondominium(userId: String) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            getUserCondominiumUseCase(userId)
                .onSuccess { condo ->
                    if (condo != null) {
                        _condominiumState.value = CondominiumState.CondominiumFound(condo)
                    } else {
                        _condominiumState.value = CondominiumState.CondominiumNotFound
                    }
                }
                .onFailure { error ->
                    _condominiumState.value = CondominiumState.Error(error.message ?: "Erro desconhecido")
                }
        }
    }

    fun searchCondominiumByCep(cep: String) {
        viewModelScope.launch {
            searchCondominiumByCepUseCase(cep)
                .onSuccess { list ->
                    _searchResults.value = list
                }
                .onFailure {
                    _searchResults.value = emptyList()
                }
        }
    }

    fun saveCondominiumCreated(userId: String, condominium: Condominium) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            saveCondominiumUseCase(userId, condominium)
                .onSuccess {
                    saveCondominiumSelected(userId, condominium)
                }
                .onFailure { error ->
                    _condominiumState.value = CondominiumState.Error(error.message ?: "Erro ao salvar")
                }
        }
    }

    fun saveCondominiumSelected(userId: String, condominium: Condominium) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            updateUserCondominiumUseCase(
                userId = userId,
                condominiumName = condominium.name,
                cep = condominium.cep,
                condominiumId = UUID.randomUUID().toString()
            ).onSuccess {
                _condominiumState.value = CondominiumState.CondominiumSaved(condominium)
            }.onFailure { error ->
                _condominiumState.value = CondominiumState.Error(error.message ?: "Erro ao vincular condomínio")
            }
        }
    }
}
