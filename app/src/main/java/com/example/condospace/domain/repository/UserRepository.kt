package com.example.condospace.domain.repository

import com.example.condospace.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User?>
    suspend fun updateUserCondominium(userId: String, condominiumName: String, cep: String): Result<Unit>
}
