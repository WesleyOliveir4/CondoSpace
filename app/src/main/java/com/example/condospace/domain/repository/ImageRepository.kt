package com.example.condospace.domain.repository

import android.net.Uri
import com.example.condospace.domain.entity.PublicationImageEntity

interface ImageRepository {
    suspend fun uploadImages(uris: List<Uri>, folder: String): Result<List<PublicationImageEntity>>
    suspend fun deleteImages(publicIds: List<String>): Result<Unit>
    suspend fun deleteFolder(folderPath: String): Result<Unit>
}
