package com.example.condospace.di.favorites

import com.example.condospace.presentation.ui.feature.favorites.viewmodel.FavoritesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {
    viewModel { FavoritesViewModel(get()) }
}
