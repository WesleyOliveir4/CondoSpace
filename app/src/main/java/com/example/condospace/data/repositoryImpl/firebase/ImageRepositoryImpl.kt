package com.example.condospace.data.repositoryImpl.firebase

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest
import kotlin.coroutines.resume

class ImageRepositoryImpl(
    private val context: Context
) : ImageRepository {

    private val cloudName = "ddrifdmzf"
    private val apiKey = "943519211491787"
    private val apiSecret = "x-Us9JjsrnS6uAETtJclDPVdjcQ"

    init {
        try {
            val config = mapOf(
                "cloud_name" to cloudName,
                "api_key" to apiKey,
                "api_secret" to apiSecret
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
        }
    }

    override suspend fun uploadImages(uris: List<Uri>, folder: String): Result<List<PublicationImageEntity>> {
        return try {
            val uploadedImages = mutableListOf<PublicationImageEntity>()
            
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

    private suspend fun uploadSingleImage(uri: Uri, folder: String): Result<PublicationImageEntity> =
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
                            continuation.resume(Result.success(PublicationImageEntity(url, publicId)))
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

    override suspend fun deleteImages(publicIds: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            for (publicId in publicIds) {
                val timestamp = System.currentTimeMillis() / 1000
                val signature = generateSignatureForDestroy(publicId, timestamp)

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("public_id", publicId)
                    .addFormDataPart("api_key", apiKey)
                    .addFormDataPart("timestamp", timestamp.toString())
                    .addFormDataPart("signature", signature)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$cloudName/image/destroy")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to delete image $publicId: ${response.body?.string()}"))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFolder(folderPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val credentials = Credentials.basic(apiKey, apiSecret)
            
            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/folders/$folderPath")
                .delete()
                .addHeader("Authorization", credentials)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete folder $folderPath: ${response.body?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateSignatureForDestroy(publicId: String, timestamp: Long): String {
        val stringToSign = "public_id=$publicId&timestamp=$timestamp$apiSecret"
        return sha1(stringToSign)
    }

    private fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
