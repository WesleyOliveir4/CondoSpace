package com.example.condospace.domain.usecase.condominium

import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.repository.CondominiumRepository

class SearchCondominiumByCepUseCase(
    private val repository: CondominiumRepository
) {
    suspend operator fun invoke(cep: String): Result<List<CondominiumEntity>> {
        if (cep.length < 8) return Result.success(emptyList())
        return repository.searchByCep(cep)
    }
}
