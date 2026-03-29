package com.example.condospace.data.repositoryImpl

import com.example.condospace.domain.model.Condominium
import com.example.condospace.domain.repository.CondominiumRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CondominiumRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CondominiumRepository {

    override suspend fun searchByCep(cep: String): Result<List<Condominium>> {
        return try {
            val document = firestore.collection("Condominium")
                .document(cep)
                .get()
                .await()

            val localCondos = document.get("localCondominiums") as? List<String>
            val result = localCondos?.map { name ->
                Condominium(name = name, cep = cep)
            } ?: emptyList()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCondominium(condominium: Condominium): Result<Unit> {
        return try {
            firestore.collection("Condominium")
                .document(condominium.cep)
                .update("localCondominiums", FieldValue.arrayUnion(condominium.name))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            try {
                firestore.collection("Condominium")
                    .document(condominium.cep)
                    .set(mapOf("localCondominiums" to listOf(condominium.name)))
                    .await()
                Result.success(Unit)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }
}