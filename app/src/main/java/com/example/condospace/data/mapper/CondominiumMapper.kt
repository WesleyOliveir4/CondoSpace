package com.example.condospace.data.mapper

import com.example.condospace.data.model.Condominium
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.CondominiumEntity
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Condominium.toEntity(): CondominiumEntity {
    return CondominiumEntity(
        id = this.id,
        name = this.name,
        cep = this.cep
    )
}

@OptIn(ExperimentalUuidApi::class)
fun CondominiumEntity.toModel(): Condominium {
    return Condominium(
        id = this.id,
        name = this.name,
        cep = this.cep
    )
}
