package com.example.condospace.di.register

import com.example.condospace.data.repositoryImpl.firebase.AuthRepositoryImpl
import com.example.condospace.data.repositoryImpl.dataStore.UserRepositoryImpl
import com.example.condospace.data.repositoryImpl.firebase.CondominiumRepositoryImpl
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.repository.CondominiumRepository
import com.example.condospace.domain.usecase.register.CreateUserUseCase
import com.example.condospace.domain.usecase.register.SignUpUseCase
import com.example.condospace.domain.usecase.condominium.GetUserCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.SearchCondominiumByCepUseCase
import com.example.condospace.domain.usecase.condominium.SaveCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.UpdateUserCondominiumUseCase
import com.example.condospace.presentation.ui.feature.register.viewmodel.RegisterViewModel
import com.example.condospace.presentation.ui.feature.condominium.viewmodel.SelectCondominiumViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val registerModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<CondominiumRepository> { CondominiumRepositoryImpl(get()) }

    factory { CreateUserUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { GetUserCondominiumUseCase(get()) }
    factory { SearchCondominiumByCepUseCase(get()) }
    factory { SaveCondominiumUseCase(get(), get()) }
    factory { UpdateUserCondominiumUseCase(get()) }

    viewModel { RegisterViewModel(
        createUserUseCase = get(),
        signUpUseCase = get()
    ) }
    
    viewModel { SelectCondominiumViewModel(
        getUserCondominiumUseCase = get(),
        searchCondominiumByCepUseCase = get(),
        saveCondominiumUseCase = get(),
        updateUserCondominiumUseCase = get()
    ) }
}
