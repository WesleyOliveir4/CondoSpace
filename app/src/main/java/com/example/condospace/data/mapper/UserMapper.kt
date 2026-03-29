package com.example.condospace.data.mapper

import com.example.condospace.data.entity.UserEntity
import com.example.condospace.domain.model.User
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun User.toEntity(): UserEntity {
    return UserEntity(
        uuid = this.uuid,
        name = this.name,
        phoneNumber = this.phoneNumber,
        profilePicture = this.profilePicture,
        email = this.email,
        cep = this.cep,
        condominiumName = this.condominiumName
    )
}

@OptIn(ExperimentalUuidApi::class)
fun UserEntity.toDomain(): User {
    return User(
        uuid = this.uuid,
        name = this.name,
        phoneNumber = this.phoneNumber,
        profilePicture = this.profilePicture ?: "",
        email = this.email,
        cep = this.cep ?: "",
        condominiumName = this.condominiumName ?: ""
    )
}
