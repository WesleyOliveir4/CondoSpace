package com.example.condospace.domain.repository

import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userData: Flow<User?>
    suspend fun saveUserData(user: UserEntity)
    suspend fun clearUserData()
    suspend fun getUserData(): UserEntity?

}
