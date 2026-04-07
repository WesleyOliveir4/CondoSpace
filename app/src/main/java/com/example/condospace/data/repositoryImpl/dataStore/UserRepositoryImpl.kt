package com.example.condospace.data.repositoryImpl.dataStore

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.data.mapper.toEntity
import com.example.condospace.data.mapper.toModel
import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun createUser(user: UserEntity): Result<Unit> {
        return try {
            val user = user.toModel()
            firestore.collection("users")
                .document(user.uuid)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<UserEntity?> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val userEntity = document.toObject(User::class.java)
            Result.success(userEntity?.toEntity())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserCondominium(
        userId: String,
        condominium: CondominiumEntity
    ): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "condominium" to condominium.toModel(),
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFavoritePublications(
        userId: String,
        publicationsIds: List<String>
    ): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update("publicationsIdFavored", publicationsIds)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            val userModel = user.toModel()
            firestore.collection("users")
                .document(userModel.uuid)
                .set(userModel)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}