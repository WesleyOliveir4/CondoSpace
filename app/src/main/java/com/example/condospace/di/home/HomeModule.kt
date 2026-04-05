package com.example.condospace.di.home

import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumUseCase
import com.example.condospace.presentation.ui.feature.home.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    factory { GetPublicationsByCondominiumUseCase(get()) }
    viewModel { HomeViewModel(get(), get()) }
}
