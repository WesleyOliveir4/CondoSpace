package com.example.condospace.domain.repository

import android.net.Uri
import com.example.condospace.domain.model.PublicationImage

interface ImageRepository {
    suspend fun uploadImages(uris: List<Uri>, folder: String): Result<List<PublicationImage>>
}
