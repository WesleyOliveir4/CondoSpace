package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.entity.UserEntity

interface UserRepository {
    suspend fun createUser(user: UserEntity): Result<Unit>
    suspend fun getUser(userId: String): Result<UserEntity?>
    suspend fun updateUserCondominium(userId: String, condominium: CondominiumEntity): Result<Unit>
    suspend fun updateFavoritePublications(userId: String, publicationsIds: List<String>): Result<Unit>
}
