package com.example.condospace.di.favorites

import com.example.condospace.domain.usecase.publication.GetFavoritePublicationsUseCase
import com.example.condospace.presentation.ui.feature.favorites.viewmodel.FavoritesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {
    factory { GetFavoritePublicationsUseCase(get(), get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
}
