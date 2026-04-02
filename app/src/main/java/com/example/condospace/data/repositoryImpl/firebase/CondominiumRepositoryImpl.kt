package com.example.condospace.data.repositoryImpl.firebase

import com.example.condospace.data.mapper.toEntity
import com.example.condospace.data.model.Condominium
import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.CondominiumRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CondominiumRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CondominiumRepository {

    override suspend fun searchByCep(cep: String): Result<List<CondominiumEntity>> {
        return try {
            val document = firestore.collection("condominium")
                .document(cep)
                .get()
                .await()

            val localCondos = document.get("localCondominiums") as? List<Map<String, Any>>
            val result = localCondos?.map { map ->
                Condominium(
                    id = map["id"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    cep = cep
                ).toEntity()
            } ?: emptyList()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCondominium(condominium: CondominiumEntity): Result<Unit> {
        val condoData = mapOf(
            "id" to condominium.id,
            "name" to condominium.name
        )
        return try {
            firestore.collection("condominium")
                .document(condominium.cep)
                .update("localCondominiums", FieldValue.arrayUnion(condoData))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            try {
                firestore.collection("condominium")
                    .document(condominium.cep)
                    .set(mapOf("localCondominiums" to listOf(condoData)))
                    .await()
                Result.success(Unit)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }
}