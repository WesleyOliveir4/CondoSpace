package com.example.condospace.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.condospace.data.model.User
import com.example.condospace.data.remote.OpenCageService
import com.example.condospace.data.remote.ViaCepService
import com.example.condospace.di.datastore.dataModule
import com.example.condospace.di.favorites.favoritesModule
import com.example.condospace.di.home.homeModule
import com.example.condospace.di.login.loginModule
import com.example.condospace.di.profile.profileModule
import com.example.condospace.di.publications.publicationsModule
import com.example.condospace.di.publish.publishModule
import com.example.condospace.di.register.registerModule
import com.example.condospace.di.retrofit.networkModule
import com.example.condospace.domain.repository.CondominiumRepository
import com.example.condospace.domain.usecase.condominium.GetUserCondominiumUseCase
import com.example.condospace.domain.usecase.condominium.SearchCondominiumByCepUseCase
import com.example.condospace.domain.usecase.condominium.UpdateUserCondominiumUseCase
import com.example.condospace.domain.usecase.publication.CreatePublicationUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationsByCondominiumUseCase
import com.example.condospace.domain.usecase.user.GetUserUseCase
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class DIModuleTest : KoinTest {

    private val mockedContext = mockk<Context>(relaxed = true)

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `check all koin modules`() {
        val testModule = module {
            single { mockk<FirebaseAuth>(relaxed = true) }
            single { mockk<FirebaseFirestore>(relaxed = true) }
            single { mockk<DataStore<User?>>(relaxed = true) }
            single { mockk<CondominiumRepository>(relaxed = true) }
            
            single { mockk<GetPublicationsByCondominiumUseCase>(relaxed = true) }
            single { mockk<GetUserUseCase>(relaxed = true) }
            single { mockk<UpdateUserUseCase>(relaxed = true) }
            single { mockk<SearchCondominiumByCepUseCase>(relaxed = true) }
            single { mockk<GetUserCondominiumUseCase>(relaxed = true) }
            single { mockk<UpdateUserCondominiumUseCase>(relaxed = true) }
            single { mockk<CreatePublicationUseCase>(relaxed = true) }

            single { mockk<ViaCepService>(relaxed = true) }
            single { mockk<OpenCageService>(relaxed = true) }
        }

        startKoin {
            androidContext(mockedContext)
            modules(
                networkModule,
                dataModule,
                loginModule,
                registerModule,
                homeModule,
                profileModule,
                publicationsModule,
                publishModule,
                favoritesModule,
                testModule
            )
        }.checkModules()
    }
}
