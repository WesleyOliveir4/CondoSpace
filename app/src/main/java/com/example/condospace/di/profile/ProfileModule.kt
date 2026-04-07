package com.example.condospace.di.profile

import com.example.condospace.data.repositoryImpl.firebase.PublicationRepositoryImpl
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.usecase.user.GetUserUseCase
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.example.condospace.presentation.ui.feature.profile.viewmodel.ChangePasswordViewModel
import com.example.condospace.presentation.ui.feature.profile.viewmodel.ProfileViewModel
import com.example.condospace.presentation.ui.feature.profile.viewmodel.SettingsViewModel
import com.example.condospace.presentation.ui.feature.profile.viewmodel.UserDataViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    factory { GetUserUseCase(get()) }
    factory { UpdateUserUseCase(get(), get()) }
    single<PublicationRepository> { PublicationRepositoryImpl(get()) }

    viewModel { ProfileViewModel(get(), get()) }
    viewModel { UserDataViewModel(get(), get(), get(), get(), get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
