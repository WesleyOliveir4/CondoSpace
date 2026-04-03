package com.example.condospace.di.publish

import com.example.condospace.domain.usecase.publication.GetPublicationsByUserUseCase
import com.example.condospace.presentation.ui.feature.publish.viewmodel.PublishViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val publishModule = module {
    factory { GetPublicationsByUserUseCase(get()) }
    viewModel { PublishViewModel(
        get(),
        get(),
        get(),
        get())
    }
}
