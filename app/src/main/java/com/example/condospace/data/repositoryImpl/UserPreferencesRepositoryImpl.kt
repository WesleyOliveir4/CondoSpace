package com.example.condospace.data.repositoryImpl

import androidx.datastore.core.DataStore
import com.example.condospace.domain.model.User
import com.example.condospace.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<User?>
) : UserPreferencesRepository {

    override val userData: Flow<User?> = dataStore.data

    override suspend fun saveUserData(user: User) {
        dataStore.updateData { user }
    }

    override suspend fun clearUserData() {
        dataStore.updateData { null }
    }

    override suspend fun getUserData(): User? {
        return dataStore.data.first()
    }
}
