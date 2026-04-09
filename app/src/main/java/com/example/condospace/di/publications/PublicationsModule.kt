package com.example.condospace.di.publications

import com.example.condospace.data.repositoryImpl.dataStore.UserPreferencesRepositoryImpl
import com.example.condospace.data.repositoryImpl.dataStore.UserRepositoryImpl
import com.example.condospace.data.repositoryImpl.firebase.PublicationRepositoryImpl
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumAndTypeUseCase
import com.example.condospace.presentation.ui.feature.publications.viewmodel.EditPublicationViewModel
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationSelectedViewModel
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val publicationsModule = module {
    factory { GetPublicationByIdUseCase(get(), get()) }
    factory { GetPublicationsByCondominiumAndTypeUseCase(get(), get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PublicationRepository> { PublicationRepositoryImpl(get()) }

    viewModel { EditPublicationViewModel(get()) }
    viewModel { PublicationSelectedViewModel(get(), get(), get(), get()) }
    viewModel { PublicationsListViewModel(get()) }
}
