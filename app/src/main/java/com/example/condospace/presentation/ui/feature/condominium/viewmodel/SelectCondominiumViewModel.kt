package com.example.condospace.presentation.ui.feature.condominium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.data.model.Condominium
import com.example.condospace.domain.usecase.condominium.GetUserCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.SaveCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.SearchCondominiumByCepUseCase
import com.example.condospace.domain.usecase.condominium.UpdateUserCondominiumUseCase
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.model.toEntity
import com.example.condospace.presentation.model.toUiModel
import com.example.condospace.presentation.ui.feature.condominium.state.CondominiumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _searchResults = MutableStateFlow<List<CondominiumUiModel>>(emptyList())
    val searchResults: StateFlow<List<CondominiumUiModel>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun resetActionState() {
        _condominiumState.update { state ->
            if (state is CondominiumState.Success) {
                state.copy(saveSuccess = false, error = null)
            } else state
        }
    }

    fun fetchUserCondominium(userId: String) {
        viewModelScope.launch {
            _condominiumState.value = CondominiumState.Loading
            getUserCondominiumUseCase(userId)
                .onSuccess { condo ->
                    _condominiumState.value = CondominiumState.Success(
                        selectedCondominium = condo?.toUiModel()
                    )
                }
                .onFailure { error ->
                    _condominiumState.value = CondominiumState.Error(error.message ?: "Erro desconhecido")
                }
        }
    }

    fun searchCondominiumByCep(cep: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = emptyList()
            searchCondominiumByCepUseCase(cep)
                .onSuccess { list ->
                    _searchResults.value = list.toUiModel()
                }
                .onFailure {
                    _searchResults.value = emptyList()
                }
            _isSearching.value = false
        }
    }

    fun saveCondominiumCreated(userId: String, condominiumUiModel: CondominiumUiModel) {
        viewModelScope.launch {
            updateSavingState(true)
            val newCondo = condominiumUiModel.copy(id = UUID.randomUUID().toString())
            saveCondominiumUseCase(userId, newCondo.toEntity())
                .onSuccess {
                    saveCondominiumSelected(userId, newCondo)
                }
                .onFailure { error ->
                    updateError(error.message ?: "Erro ao salvar")
                }
        }
    }

    fun saveCondominiumSelected(userId: String, condominium: CondominiumUiModel) {
        viewModelScope.launch {
            updateSavingState(true)
            updateUserCondominiumUseCase(
                userId = userId,
                condominiumEntity = Condominium(
                    id = condominium.id,
                    name = condominium.name,
                    cep = condominium.cep
                ).toEntity()
            ).onSuccess {
                _condominiumState.update { state ->
                    if (state is CondominiumState.Success) {
                        state.copy(
                            selectedCondominium = condominium,
                            isSaving = false,
                            saveSuccess = true
                        )
                    } else {
                        CondominiumState.Success(
                            selectedCondominium = condominium,
                            saveSuccess = true
                        )
                    }
                }
            }.onFailure { error ->
                updateError(error.message ?: "Erro ao vincular condomínio")
            }
        }
    }

    private fun updateSavingState(isSaving: Boolean) {
        _condominiumState.update { state ->
            if (state is CondominiumState.Success) state.copy(isSaving = isSaving)
            else state
        }
    }

    private fun updateError(message: String) {
        _condominiumState.update { state ->
            if (state is CondominiumState.Success) state.copy(isSaving = false, error = message)
            else CondominiumState.Error(message)
        }
    }
}
