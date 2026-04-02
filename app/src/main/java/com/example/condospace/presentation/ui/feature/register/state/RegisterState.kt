package com.example.condospace.presentation.ui.feature.register.state

sealed class RegisterState{
    data class Registered(val userUuid : String) : RegisterState()
    object NotRegistered : RegisterState()
    object Loading : RegisterState()
    data class Error(val message : String) : RegisterState()
}