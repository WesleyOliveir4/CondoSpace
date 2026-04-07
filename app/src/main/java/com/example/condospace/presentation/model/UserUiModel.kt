package com.example.condospace.presentation.model

import com.example.condospace.domain.entity.UserEntity


data class UserUiModel (
    val uuid: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profilePicture: String? = null,
    val email: String = "",
    val condominium: CondominiumUiModel? = null,
    val publicationsIdFavored: List<String>? = null,
    val userIsLogged: Boolean = true
)

fun UserEntity.toUiModel(): UserUiModel{
    return UserUiModel(
        uuid = uuid,
        name = name,
        phoneNumber = phoneNumber,
        profilePicture = profilePicture,
        email = email,
        condominium = condominiumEntity?.toUiModel(),
        publicationsIdFavored = publicationsIdFavored,
        userIsLogged = userIsLogged
    )
}

fun UserUiModel.toEntity(): UserEntity{
    return UserEntity(
        uuid = uuid,
        name = name,
        phoneNumber = phoneNumber,
        profilePicture = profilePicture,
        email = email,
        condominiumEntity = condominium?.toEntity(),
        publicationsIdFavored = publicationsIdFavored,
        userIsLogged = userIsLogged
    )
}