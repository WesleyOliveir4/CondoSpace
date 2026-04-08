package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userData: Flow<UserEntity?>
    suspend fun saveUserData(user: UserEntity)
    suspend fun clearUserData()
    suspend fun getUserData(): UserEntity?
}
