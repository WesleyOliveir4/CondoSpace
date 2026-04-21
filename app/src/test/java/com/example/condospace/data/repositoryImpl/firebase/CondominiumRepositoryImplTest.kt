package com.example.condospace.data.repositoryImpl.firebase

import com.example.condospace.domain.entity.CondominiumEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CondominiumRepositoryImplTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: CondominiumRepositoryImpl
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference

    @Before
    fun setUp() {
        firestore = mockk()
        collectionReference = mockk()
        documentReference = mockk()
        repository = CondominiumRepositoryImpl(firestore)

        every { firestore.collection("condominium") } returns collectionReference
        
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        mockkStatic(FieldValue::class)
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
        unmockkStatic(FieldValue::class)
    }

    @Test
    fun `searchByCep should return list of condominium entities when successful`() = runTest {
        // Arrange
        val cep = "12345678"
        val snapshot = mockk<DocumentSnapshot>()
        val task = mockk<Task<DocumentSnapshot>>()
        val localCondos = listOf(
            mapOf("id" to "1", "name" to "Condo A"),
            mapOf("id" to "2", "name" to "Condo B")
        )

        every { collectionReference.document(cep) } returns documentReference
        every { documentReference.get() } returns task
        coEvery { task.await() } returns snapshot
        every { snapshot.get("localCondominiums") } returns localCondos

        // Act
        val result = repository.searchByCep(cep)

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(2, list?.size)
        assertEquals("Condo A", list?.get(0)?.name)
        assertEquals(cep, list?.get(0)?.cep)
    }

    @Test
    fun `searchByCep should return empty list when no condominiums found`() = runTest {
        // Arrange
        val cep = "12345678"
        val snapshot = mockk<DocumentSnapshot>()
        val task = mockk<Task<DocumentSnapshot>>()

        every { collectionReference.document(cep) } returns documentReference
        every { documentReference.get() } returns task
        coEvery { task.await() } returns snapshot
        every { snapshot.get("localCondominiums") } returns null

        // Act
        val result = repository.searchByCep(cep)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `searchByCep should return failure when exception occurs`() = runTest {
        // Arrange
        val cep = "12345678"
        val exception = Exception("Firestore error")
        val task = mockk<Task<DocumentSnapshot>>()

        every { collectionReference.document(cep) } returns documentReference
        every { documentReference.get() } returns task
        coEvery { task.await() } throws exception

        // Act
        val result = repository.searchByCep(cep)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `saveCondominium should use arrayUnion when update is successful`() = runTest {
        // Arrange
        val condo = CondominiumEntity(id = "1", name = "Condo A", cep = "12345")
        val task = mockk<Task<Void>>()
        val mockFieldValue = mockk<FieldValue>()
        
        every { FieldValue.arrayUnion(any()) } returns mockFieldValue
        every { collectionReference.document(condo.cep) } returns documentReference
        every { documentReference.update("localCondominiums", mockFieldValue) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.saveCondominium(condo)

        // Assert
        assertTrue(result.isSuccess)
        verify { documentReference.update("localCondominiums", mockFieldValue) }
    }

    @Test
    fun `saveCondominium should use set when update fails`() = runTest {
        // Arrange
        val condo = CondominiumEntity(id = "1", name = "Condo A", cep = "12345")
        val updateTask = mockk<Task<Void>>()
        val setTask = mockk<Task<Void>>()
        val mockFieldValue = mockk<FieldValue>()

        every { FieldValue.arrayUnion(any()) } returns mockFieldValue
        every { collectionReference.document(condo.cep) } returns documentReference
        every { documentReference.update("localCondominiums", mockFieldValue) } returns updateTask
        coEvery { updateTask.await() } throws Exception("Update failed")
        
        every { documentReference.set(any()) } returns setTask
        coEvery { setTask.await() } returns mockk()

        // Act
        val result = repository.saveCondominium(condo)

        // Assert
        assertTrue(result.isSuccess)
        verify { documentReference.set(any()) }
    }

    @Test
    fun `saveCondominium should return failure when both update and set fail`() = runTest {
        // Arrange
        val condo = CondominiumEntity(id = "1", name = "Condo A", cep = "12345")
        val updateTask = mockk<Task<Void>>()
        val setTask = mockk<Task<Void>>()
        val exception = Exception("Set failed")

        every { FieldValue.arrayUnion(any()) } returns mockk()
        every { collectionReference.document(condo.cep) } returns documentReference
        every { documentReference.update(any<String>(), any()) } returns updateTask
        coEvery { updateTask.await() } throws Exception("Update failed")
        
        every { documentReference.set(any()) } returns setTask
        coEvery { setTask.await() } throws exception

        // Act
        val result = repository.saveCondominium(condo)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
