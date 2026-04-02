package com.example.condospace.data.repositoryImpl.firebase

import com.example.condospace.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User is null")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}
