package com.example.condospace.data.repositoryImpl

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

import com.example.condospace.domain.model.PublicationImage
import com.example.condospace.domain.repository.ImageRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ImageRepositoryImpl(
    private val context: Context
) : ImageRepository {

    init {
        try {
            val config = mapOf(
                "cloud_name" to "ddrifdmzf",
                "api_key" to "943519211491787",
                "api_secret" to "x-Us9JjsrnS6uAETtJclDPVdjcQ"
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
        }
    }

    override suspend fun uploadImages(uris: List<Uri>, folder: String): Result<List<PublicationImage>> {
        return try {
            val uploadedImages = mutableListOf<PublicationImage>()
            
            for (uri in uris) {
                val result = uploadSingleImage(uri, folder)
                if (result.isSuccess) {
                    result.getOrNull()?.let { uploadedImages.add(it) }
                } else {
                    return Result.failure(result.exceptionOrNull() ?: Exception("Upload failed"))
                }
            }
            Result.success(uploadedImages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadSingleImage(uri: Uri, folder: String): Result<PublicationImage> = 
        suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(uri)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val url = resultData?.get("secure_url") as? String
                        val publicId = resultData?.get("public_id") as? String
                        if (url != null && publicId != null) {
                            continuation.resume(Result.success(PublicationImage(url, publicId)))
                        } else {
                            continuation.resume(Result.failure(Exception("Cloudinary response missing data")))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        continuation.resume(Result.failure(Exception(error?.description ?: "Upload error")))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        continuation.resume(Result.failure(Exception("Upload rescheduled")))
                    }
                }).dispatch()
        }
}
