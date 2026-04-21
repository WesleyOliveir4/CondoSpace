package com.example.condospace.data.repositoryImpl.dataStore

import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.entity.CondominiumEntity
import com.example.condospace.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: UserRepositoryImpl
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference

    @Before
    fun setUp() {
        firestore = mockk()
        collectionReference = mockk()
        documentReference = mockk()

        every { firestore.collection("users") } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference

        repository = UserRepositoryImpl(firestore)
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `createUser should return success when firestore set succeeds`() = runTest {
        // Arrange
        val userEntity = mockk<UserEntity>(relaxed = true)
        every { userEntity.uuid } returns "user123"
        val task = mockk<Task<Void>>()
        
        every { documentReference.set(any()) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.createUser(userEntity)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getUser should return user entity when document exists`() = runTest {
        // Arrange
        val userId = "user123"
        val snapshot = mockk<DocumentSnapshot>()
        val userModel = User(uuid = userId, name = "Test User", email = "test@test.com", phoneNumber = "")
        val task = mockk<Task<DocumentSnapshot>>()

        every { documentReference.get() } returns task
        coEvery { task.await() } returns snapshot
        every { snapshot.toObject(User::class.java) } returns userModel

        // Act
        val result = repository.getUser(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(userId, result.getOrNull()?.uuid)
        assertEquals("Test User", result.getOrNull()?.name)
    }

    @Test
    fun `updateUserCondominium should return success when update succeeds`() = runTest {
        // Arrange
        val userId = "user123"
        val condo = CondominiumEntity(id = "condo1", name = "Condo 1", cep = "12345678")
        val task = mockk<Task<Void>>()

        every { documentReference.update(any<Map<String, Any>>()) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.updateUserCondominium(userId, condo)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateFavoritePublications should return success when update succeeds`() = runTest {
        // Arrange
        val userId = "user123"
        val ids = listOf("pub1", "pub2")
        val task = mockk<Task<Void>>()

        every { documentReference.update("publicationsIdFavored", ids) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.updateFavoritePublications(userId, ids)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateUser should return success when set succeeds`() = runTest {
        // Arrange
        val userEntity = mockk<UserEntity>(relaxed = true)
        every { userEntity.uuid } returns "user123"
        val task = mockk<Task<Void>>()

        every { documentReference.set(any()) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.updateUser(userEntity)

        // Assert
        assertTrue(result.isSuccess)
    }
}
