package com.example.condospace.presentation.ui.feature.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.data.model.User
import com.example.condospace.domain.usecase.register.CreateUserUseCase
import com.example.condospace.domain.usecase.register.SignUpUseCase
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.model.toEntity
import com.example.condospace.presentation.ui.feature.register.state.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.NotRegistered)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun signup(name: String, email: String, password: String, phone: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            _registerState.value = RegisterState.Error("All fields are required")
            return
        }

        _registerState.value = RegisterState.Loading
        
        viewModelScope.launch {
            val signUpResult = signUpUseCase(email, password)
            
            signUpResult.onSuccess { uid ->
                val newUser = UserUiModel(
                    uuid = uid,
                    name = name,
                    phoneNumber = phone,
                    profilePicture = "",
                    email = email,
                    condominium = null
                )
                saveUserToFirestore(newUser)
            }.onFailure { exception ->
                _registerState.value = RegisterState.Error(exception.message ?: "SignUp failed")
            }
        }
    }

    private suspend fun saveUserToFirestore(user: UserUiModel) {
        val result = createUserUseCase(user.toEntity())
        if (result.isSuccess) {
            _registerState.value = RegisterState.Registered(
                userUuid = user.uuid
            )
        } else {
            _registerState.value = RegisterState.Error(result.exceptionOrNull()?.message ?: "Failed to save user data")
        }
    }
}

