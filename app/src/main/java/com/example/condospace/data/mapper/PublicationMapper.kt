package com.example.condospace.data.mapper

import com.example.condospace.data.model.Condominium
import com.example.condospace.data.model.Publication
import com.example.condospace.data.model.PublicationImage
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.data.model.User
import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Publication.toEntity(): PublicationEntity {
    return PublicationEntity(
        id = this.id,
        publicationOwnerUuid = this.publicationOwnerUuid,
        publicationCondominiumId = this.publicationCondominiumId,
        publicationOwner = this.publicationOwner,
        serviceProvider = this.serviceProvider,
        contact = this.contact,
        imageUrlList = this.imageUrlList?.toEntity(),
        imagesSelectList = this.imagesSelectList,
        title = this.title,
        description = this.description,
        publicationType = this.publicationType,
        price = this.price,
        likes = this.likes,
        date = this.date
    )
}

fun List<PublicationImage>.toEntity(): List<PublicationImageEntity>{
    return this.map {
        PublicationImageEntity(
            url = it.url,
            publicId = it.publicId
        )
    }
}
