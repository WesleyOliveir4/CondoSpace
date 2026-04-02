package com.example.condospace.di.publish

import com.example.condospace.presentation.ui.feature.publish.viewmodel.PublishViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val publishModule = module {
    viewModel { PublishViewModel(get()) }
}
