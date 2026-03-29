package com.example.condospace.presentation.ui.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condospace.domain.usecase.SignInUseCase
import com.example.condospace.presentation.ui.feature.login.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Unauthenticated)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _loginState.value = LoginState.Error("Email or password can't be empty")
            return
        }
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            val result = signInUseCase(email, password)
            if (result.isSuccess) {
                _loginState.value = LoginState.Authenticated
            } else {
                _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }


}