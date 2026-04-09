package com.example.condospace.data.repositoryImpl.firebase

import com.example.condospace.data.mapper.toEntity
import com.example.condospace.data.mapper.toModel
import com.example.condospace.data.model.Publication
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.repository.PublicationRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PublicationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PublicationRepository {

    override suspend fun createPublication(publication: PublicationEntity): Result<Unit> {
        return try {
            firestore.collection("publications")
                .document(publication.id)
                .set(publication)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicationsByCondominium(condominiumId: String): Result<List<PublicationEntity>> {
        return try {
            val snapshot = firestore.collection("publications")
                .whereEqualTo("publicationCondominiumId", condominiumId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val publications = snapshot.toObjects(Publication::class.java).map {
                it.toEntity()
            }
            Result.success(publications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicationsByCondominiumAndType(condominiumId: String, type: String?): Result<List<PublicationEntity>> {
        return try {
            var query = firestore.collection("publications")
                .whereEqualTo("publicationCondominiumId", condominiumId)
            
            if (type != null && type != "Todos") {
                query = query.whereEqualTo("publicationType", type)
            }
            
            val snapshot = query.orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val publications = snapshot.toObjects(Publication::class.java).map {
                it.toEntity()
            }
            Result.success(publications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPublicationsByUser(userId: String): Flow<Result<List<PublicationEntity>>> = callbackFlow {
        val subscription = firestore.collection("publications")
            .whereEqualTo("publicationOwnerUuid", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val publications = snapshot.toObjects(Publication::class.java).map {
                        it.toEntity()
                    }
                    trySend(Result.success(publications))
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getPublicationById(id: String): Result<PublicationEntity> {
        return try {
            val snapshot = firestore.collection("publications")
                .document(id)
                .get()
                .await()
            
            val publication = snapshot.toObject(Publication::class.java)
            if (publication != null) {
                Result.success(publication.toEntity())
            } else {
                Result.failure(Exception("Publicação não encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePublication(publicationId: String): Result<Unit> {
        return try {
            firestore.collection("publications")
                .document(publicationId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePublicationLikes(publicationId: String, increment: Int): Result<Unit> {
        return try {
            firestore.collection("publications")
                .document(publicationId)
                .update("likes", FieldValue.increment(increment.toLong()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePublication(publication: PublicationEntity): Result<Unit> {
        return try {
            firestore.collection("publications")
                .document(publication.id)
                .set(publication.toModel())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicationsByIds(ids: List<String>): Result<List<PublicationEntity>> {
        return try {
            if (ids.isEmpty()) return Result.success(emptyList())
            
            // Firestore 'in' operator supports up to 30 items
            val chunks = ids.chunked(30)
            val publications = mutableListOf<PublicationEntity>()
            
            for (chunk in chunks) {
                val snapshot = firestore.collection("publications")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()
                
                publications.addAll(snapshot.toObjects(Publication::class.java).map { it.toEntity() })
            }
            
            Result.success(publications.sortedByDescending { it.date })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
