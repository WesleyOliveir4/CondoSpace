package com.example.condospace.di.publications

import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.ui.feature.publications.viewmodel.EditPublicationViewModel
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationSelectedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val publicationsModule = module {
    factory { GetPublicationByIdUseCase(get()) }
    viewModel { EditPublicationViewModel(get()) }
    viewModel { PublicationSelectedViewModel(get()) }
}
