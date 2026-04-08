package com.example.condospace.di.publish

import com.example.condospace.domain.usecase.publication.CreatePublicationUseCase
import com.example.condospace.domain.usecase.publication.DeletePublicationImagesUseCase
import com.example.condospace.domain.usecase.publication.DeletePublicationUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationsByUserUseCase
import com.example.condospace.presentation.ui.feature.publish.viewmodel.PublishViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val publishModule = module {
    factory { GetPublicationsByUserUseCase(get()) }
    factory { DeletePublicationImagesUseCase(get()) }
    factory { CreatePublicationUseCase(get(), get()) }
    factory { DeletePublicationUseCase(get(), get()) }
    
    viewModel { 
        PublishViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
}
