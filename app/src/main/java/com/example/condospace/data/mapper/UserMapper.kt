package com.example.condospace.data.mapper

import com.example.condospace.data.model.Condominium
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.CondominiumEntity
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun User.toEntity(): UserEntity {
    return UserEntity(
        uuid = this.uuid,
        name = this.name,
        phoneNumber = this.phoneNumber,
        profilePicture = this.profilePicture,
        email = this.email,
        condominiumEntity = CondominiumEntity(
            id = this.condominium?.id ,
            name = this.condominium?.name ?:"",
            cep = this.condominium?.cep ?:""
        ),
        publicationsIdFavored = this.publicationsIdFavored,
        notificationsEnabled = this.notificationsEnabled
    )
}

@OptIn(ExperimentalUuidApi::class)
fun UserEntity.toModel(): User {
    return User(
        uuid = this.uuid,
        name = this.name,
        phoneNumber = this.phoneNumber,
        profilePicture = this.profilePicture ?: "",
        email = this.email,
        condominium = Condominium(
            id = this.condominiumEntity?.id ?: "",
            name = this.condominiumEntity?.name ?: "",
            cep = this.condominiumEntity?.cep ?: ""
        ),
        publicationsIdFavored = this.publicationsIdFavored,
        notificationsEnabled = this.notificationsEnabled
    )
}
