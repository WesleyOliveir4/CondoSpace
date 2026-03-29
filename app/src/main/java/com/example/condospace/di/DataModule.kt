package com.example.condospace.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.condospace.data.datastore.UserSerializer
import com.example.condospace.data.repositoryImpl.AuthRepositoryImpl
import com.example.condospace.data.repositoryImpl.UserPreferencesRepositoryImpl
import com.example.condospace.data.repositoryImpl.UserRepositoryImpl
import com.example.condospace.domain.model.User
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
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
    
    single { androidContext().userDataStore }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }
}
