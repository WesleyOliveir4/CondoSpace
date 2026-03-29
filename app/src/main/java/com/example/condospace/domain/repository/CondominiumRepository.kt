package com.example.condospace.domain.repository

import com.example.condospace.domain.model.Condominium

interface CondominiumRepository {
    suspend fun searchByCep(cep: String): Result<List<Condominium>>
    suspend fun saveCondominium(condominium: Condominium): Result<Unit>
}
