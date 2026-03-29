package com.example.condospace.di.login

import com.example.condospace.domain.usecase.SignInUseCase
import com.example.condospace.presentation.ui.feature.login.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    factory { SignInUseCase(get()) }
    
    viewModel { 
        LoginViewModel(
            signInUseCase = get(),
            authRepository = get(),
            userRepository = get(),
            userPreferencesRepository = get()
        ) 
    }
}
