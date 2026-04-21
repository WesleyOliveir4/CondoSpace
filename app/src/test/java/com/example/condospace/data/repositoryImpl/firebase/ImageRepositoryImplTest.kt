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
        // MediaManager.init returns void, so we use just runs.
        every { MediaManager.init(any<Context>(), any<Map<String, Any>>()) } just runs
        
        repository = ImageRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(MediaManager::class)
    }

    @Test
    fun `uploadImages should return success with empty list when uris list is empty`() = runTest {
        // Arrange
        val uris = emptyList<Uri>()

        // Act
        val result = repository.uploadImages(uris, "folder")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `uploadImages should return success when single image upload succeeds`() = runTest {
        // Arrange
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

        // Act
        val result = repository.uploadImages(uris, "folder")

        // Assert
        assertTrue(result.isSuccess)
        val images = result.getOrNull()
        assertEquals(1, images?.size)
        assertEquals("http://url.com", images?.first()?.url)
        assertEquals("id123", images?.first()?.publicId)
    }

    @Test
    fun `uploadImages should return failure when single image upload fails`() = runTest {
        // Arrange
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

        // Act
        val result = repository.uploadImages(uris, "folder")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Upload error", result.exceptionOrNull()?.message)
    }
}
