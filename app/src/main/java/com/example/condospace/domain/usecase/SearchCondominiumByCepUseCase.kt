package com.example.condospace.domain.usecase

import com.example.condospace.domain.model.Condominium
import com.example.condospace.domain.repository.CondominiumRepository

class SearchCondominiumByCepUseCase(
    private val repository: CondominiumRepository
) {
    suspend operator fun invoke(cep: String): Result<List<Condominium>> {
        if (cep.length < 8) return Result.success(emptyList())
        return repository.searchByCep(cep)
    }
}
