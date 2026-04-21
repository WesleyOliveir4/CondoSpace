package com.example.condospace.data.repositoryImpl.dataStore

import androidx.datastore.core.DataStore
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.data.mapper.toModel
import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<User?>
) : UserPreferencesRepository {

    override val userData: Flow<UserEntity?> = dataStore.data.map { it?.toEntity() }

    override suspend fun saveUserData(user: UserEntity) {
        dataStore.updateData { user.toModel() }
    }

    override suspend fun clearUserData() {
        dataStore.updateData { null }
    }

    override suspend fun getUserData(): UserEntity? {
        return try {
            dataStore.data.first()?.toEntity()
        } catch (e: Exception) {
            null
        }
    }
}
