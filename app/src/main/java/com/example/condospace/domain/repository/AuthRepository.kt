package com.example.condospace.domain.repository

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    fun getCurrentUserUid(): String?
    suspend fun signOut(): Result<Unit>
}
