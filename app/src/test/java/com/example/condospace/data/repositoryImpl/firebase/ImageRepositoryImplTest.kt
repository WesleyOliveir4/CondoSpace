package com.example.condospace.data.repositoryImpl.firebase

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.UploadRequest
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ImageRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var repository: ImageRepositoryImpl

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        
        // Mock MediaManager static methods
        mockkStatic(MediaManager::class)
        every { MediaManager.init(any<Context>(), any<Map<String, Any>>()) } just runs
        
        repository = ImageRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(MediaManager::class)
        io.mockk.unmockkConstructor(OkHttpClient::class)
    }

    @Test
    fun `uploadImages should return success with empty list when uris list is empty`() = runTest {
        val result = repository.uploadImages(emptyList(), "folder")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `uploadImages should return success when single image upload succeeds`() = runTest {
        val uri = mockk<Uri>()
        val uris = listOf(uri)
        val mediaManager = mockk<MediaManager>()
        val uploadRequest = mockk<UploadRequest<*>>(relaxed = true)
        val callbackSlot = slot<UploadCallback>()
        
        every { MediaManager.get() } returns mediaManager
        every { mediaManager.upload(uri) } returns uploadRequest
        every { uploadRequest.option(any(), any()) } returns uploadRequest
        every { uploadRequest.callback(capture(callbackSlot)) } returns uploadRequest
        every { uploadRequest.dispatch() } answers {
            callbackSlot.captured.onSuccess("req123", mapOf("secure_url" to "http://url.com", "public_id" to "id123"))
            "req123"
        }

        val result = repository.uploadImages(uris, "folder")
        assertTrue(result.isSuccess)
        val images = result.getOrNull()
        assertEquals(1, images?.size)
        assertEquals("http://url.com", images?.first()?.url)
    }

    @Test
    fun `uploadImages should return failure when single image upload fails`() = runTest {
        val uri = mockk<Uri>()
        val uris = listOf(uri)
        val mediaManager = mockk<MediaManager>()
        val uploadRequest = mockk<UploadRequest<*>>(relaxed = true)
        val callbackSlot = slot<UploadCallback>()
        val errorInfo = mockk<ErrorInfo>()
        every { errorInfo.description } returns "Upload error"
        
        every { MediaManager.get() } returns mediaManager
        every { mediaManager.upload(uri) } returns uploadRequest
        every { uploadRequest.option(any(), any()) } returns uploadRequest
        every { uploadRequest.callback(capture(callbackSlot)) } returns uploadRequest
        every { uploadRequest.dispatch() } answers {
            callbackSlot.captured.onError("req123", errorInfo)
            "req123"
        }

        val result = repository.uploadImages(uris, "folder")
        assertTrue(result.isFailure)
        assertEquals("Upload error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteImages should return success when all deletions are successful`() = runTest {
        io.mockk.mockkConstructor(OkHttpClient::class)
        val call = mockk<Call>()
        val response = Response.Builder()
            .request(Request.Builder().url("https://test.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        every { anyConstructed<OkHttpClient>().newCall(any()) } returns call
        every { call.execute() } returns response

        val result = repository.deleteImages(listOf("id1", "id2"))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteImages should return failure when any deletion fails`() = runTest {
        io.mockk.mockkConstructor(OkHttpClient::class)
        val call = mockk<Call>()
        val response = Response.Builder()
            .request(Request.Builder().url("https://test.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(400)
            .message("Bad Request")
            .body("Error".toResponseBody("text/plain".toMediaTypeOrNull()))
            .build()

        every { anyConstructed<OkHttpClient>().newCall(any()) } returns call
        every { call.execute() } returns response

        val result = repository.deleteImages(listOf("id1"))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Failed to delete image") == true)
    }

    @Test
    fun `deleteFolder should return success when deletion is successful`() = runTest {
        io.mockk.mockkConstructor(OkHttpClient::class)
        val call = mockk<Call>()
        val response = Response.Builder()
            .request(Request.Builder().url("https://test.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        every { anyConstructed<OkHttpClient>().newCall(any()) } returns call
        every { call.execute() } returns response

        val result = repository.deleteFolder("folder_path")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteFolder should return failure when deletion fails`() = runTest {
        io.mockk.mockkConstructor(OkHttpClient::class)
        val call = mockk<Call>()
        val response = Response.Builder()
            .request(Request.Builder().url("https://test.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(404)
            .message("Not Found")
            .body("Folder not found".toResponseBody("text/plain".toMediaTypeOrNull()))
            .build()

        every { anyConstructed<OkHttpClient>().newCall(any()) } returns call
        every { call.execute() } returns response

        val result = repository.deleteFolder("non_existent_folder")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Failed to delete folder") == true)
    }
}
