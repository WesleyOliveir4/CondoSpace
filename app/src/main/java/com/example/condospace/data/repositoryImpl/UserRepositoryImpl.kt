package com.example.condospace.data.repositoryImpl

import com.example.condospace.data.entity.UserEntity
import com.example.condospace.data.mapper.toDomain
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.domain.model.User
import com.example.condospace.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            val userEntity = user.toEntity()
            firestore.collection("users")
                .document(userEntity.uuid)
                .set(userEntity)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val userEntity = document.toObject(UserEntity::class.java)
            Result.success(userEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserCondominium(
        userId: String,
        condominiumName: String,
        cep: String
    ): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "condominiumName" to condominiumName,
                        "cep" to cep
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}