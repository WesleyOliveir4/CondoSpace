package com.example.condospace.di.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.condospace.data.datastore.UserSerializer
import com.example.condospace.data.repositoryImpl.firebase.AuthRepositoryImpl
import com.example.condospace.data.repositoryImpl.firebase.ImageRepositoryImpl
import com.example.condospace.data.repositoryImpl.firebase.PublicationRepositoryImpl
import com.example.condospace.data.repositoryImpl.dataStore.UserPreferencesRepositoryImpl
import com.example.condospace.data.repositoryImpl.dataStore.UserRepositoryImpl
import com.example.condospace.data.repositoryImpl.externalAPIs.ExternalServiceRepositoryImpl
import com.example.condospace.data.model.User
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.userDataStore: DataStore<User?> by dataStore(
    fileName = "user_prefs.json",
    serializer = UserSerializer
)

val dataModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PublicationRepository> { PublicationRepositoryImpl(get()) }
    single<ImageRepository> { ImageRepositoryImpl(androidContext()) }
    
    single { androidContext().userDataStore }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }

    single<ExternalServiceRepository> { ExternalServiceRepositoryImpl(get(), get(), get()) }
}
