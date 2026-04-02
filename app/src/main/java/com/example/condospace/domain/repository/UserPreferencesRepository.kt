package com.example.condospace.domain.repository

import com.example.condospace.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userData: Flow<User?>
    suspend fun saveUserData(user: User)
    suspend fun clearUserData()
    suspend fun getUserData(): User?

}
