package com.example.condospace.presentation.model

import com.example.condospace.domain.entity.CondominiumEntity


data class CondominiumUiModel(
    var id: String? = null,
    val name: String,
    val cep: String,
)

fun CondominiumEntity.toUiModel(): CondominiumUiModel {
    return CondominiumUiModel(
        id = id,
        name = name,
        cep = cep
    )
}

fun CondominiumUiModel.toEntity(): CondominiumEntity {
    return CondominiumEntity(
        id = id ?: "",
        name = name,
        cep = cep
    )
}


fun List<CondominiumEntity>.toUiModel(): List<CondominiumUiModel> {
    return map { it.toUiModel() }
}




