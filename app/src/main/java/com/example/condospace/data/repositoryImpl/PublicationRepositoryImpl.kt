package com.example.condospace.data.repositoryImpl

import com.example.condospace.domain.model.Publication
import com.example.condospace.domain.repository.PublicationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PublicationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PublicationRepository {

    override suspend fun createPublication(publication: Publication): Result<Unit> {
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

    override suspend fun getPublicationsByCondominium(condominiumName: String): Result<List<Publication>> {
        return try {
            val snapshot = firestore.collection("publications")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val publications = snapshot.toObjects(Publication::class.java)
            Result.success(publications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicationsByUser(userId: String): Result<List<Publication>> {
        return try {
            val snapshot = firestore.collection("publications")
                .whereEqualTo("publicationOwnerUuid", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val publications = snapshot.toObjects(Publication::class.java)
            Result.success(publications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
