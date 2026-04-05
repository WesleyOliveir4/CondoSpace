package com.example.condospace.di.publications

import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumAndTypeUseCase
import com.example.condospace.presentation.ui.feature.publications.viewmodel.EditPublicationViewModel
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationSelectedViewModel
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val publicationsModule = module {
    factory { GetPublicationByIdUseCase(get()) }
    factory { GetPublicationsByCondominiumAndTypeUseCase(get()) }
    viewModel { EditPublicationViewModel(get()) }
    viewModel { PublicationSelectedViewModel(get()) }
    viewModel { PublicationsListViewModel(get(), get()) }
}
