package com.example.condospace.domain.repository

import com.example.condospace.domain.entity.CondominiumEntity

interface CondominiumRepository {
    suspend fun searchByCep(cep: String): Result<List<CondominiumEntity>>
    suspend fun saveCondominium(condominium: CondominiumEntity): Result<Unit>
}
