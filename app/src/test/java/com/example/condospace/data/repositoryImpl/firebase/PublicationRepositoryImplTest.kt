package com.example.condospace.data.repositoryImpl.firebase

import com.example.condospace.data.model.Publication
import com.example.condospace.domain.entity.PublicationEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PublicationRepositoryImplTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: PublicationRepositoryImpl
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference

    @Before
    fun setUp() {
        firestore = mockk()
        collectionReference = mockk()
        documentReference = mockk()
        repository = PublicationRepositoryImpl(firestore)

        every { firestore.collection("publications") } returns collectionReference
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        mockkStatic(FieldValue::class)
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
        unmockkStatic(FieldValue::class)
    }

    @Test
    fun `createPublication should return success when firestore set is successful`() = runTest {
        // Arrange
        val publication = mockk<PublicationEntity>(relaxed = true)
        val task = mockk<Task<Void>>()
        every { publication.id } returns "pub123"
        every { collectionReference.document("pub123") } returns documentReference
        every { documentReference.set(publication) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.createPublication(publication)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getPublicationsByCondominium should return list of publications`() = runTest {
        // Arrange
        val condoId = "condo123"
        val query = mockk<Query>()
        val orderedQuery = mockk<Query>()
        val task = mockk<Task<QuerySnapshot>>()
        val snapshot = mockk<QuerySnapshot>()
        
        every { collectionReference.whereEqualTo("publicationCondominiumId", condoId) } returns query
        every { query.orderBy("date", Query.Direction.DESCENDING) } returns orderedQuery
        every { orderedQuery.get() } returns task
        coEvery { task.await() } returns snapshot
        every { snapshot.toObjects(Publication::class.java) } returns emptyList()

        // Act
        val result = repository.getPublicationsByCondominium(condoId)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deletePublication should return success when deletion is successful`() = runTest {
        // Arrange
        val pubId = "pub123"
        val task = mockk<Task<Void>>()
        every { collectionReference.document(pubId) } returns documentReference
        every { documentReference.delete() } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.deletePublication(pubId)

        // Assert
        assertTrue(result.isSuccess)
        verify { documentReference.delete() }
    }

    @Test
    fun `updatePublicationLikes should use increment correctly`() = runTest {
        // Arrange
        val pubId = "pub123"
        val increment = 1
        val task = mockk<Task<Void>>()
        val mockFieldValue = mockk<FieldValue>()
        
        every { FieldValue.increment(1L) } returns mockFieldValue
        every { collectionReference.document(pubId) } returns documentReference
        every { documentReference.update("likes", mockFieldValue) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.updatePublicationLikes(pubId, increment)

        // Assert
        assertTrue(result.isSuccess)
        verify { documentReference.update("likes", mockFieldValue) }
    }

    @Test
    fun `getPublicationsByUser should emit success list when listener receives snapshot`() = runTest {
        // Arrange
        val userId = "user123"
        val query = mockk<Query>()
        val snapshot = mockk<QuerySnapshot>()
        val registration = mockk<ListenerRegistration>()
        
        every { collectionReference.whereEqualTo("publicationOwnerUuid", userId) } returns query
        every { query.addSnapshotListener(any()) } answers {
            val listener = it.invocation.args[0] as EventListener<QuerySnapshot>
            listener.onEvent(snapshot, null)
            registration
        }
        every { snapshot.toObjects(Publication::class.java) } returns emptyList()
        every { registration.remove() } just runs

        // Act
        val result = repository.getPublicationsByUser(userId).first()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `getPublicationsByUser should emit failure when listener receives error`() = runTest {
        // Arrange
        val userId = "user123"
        val query = mockk<Query>()
        val registration = mockk<ListenerRegistration>()
        val error = mockk<com.google.firebase.firestore.FirebaseFirestoreException>()
        
        every { collectionReference.whereEqualTo("publicationOwnerUuid", userId) } returns query
        every { query.addSnapshotListener(any()) } answers {
            val listener = it.invocation.args[0] as EventListener<QuerySnapshot>
            listener.onEvent(null, error)
            registration
        }
        every { registration.remove() } just runs

        // Act
        val result = repository.getPublicationsByUser(userId).first()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `getPublicationById should return success when publication is found`() = runTest {
        // Arrange
        val pubId = "pub123"
        val task = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>()
        val snapshot = mockk<com.google.firebase.firestore.DocumentSnapshot>()
        val publication = mockk<Publication>(relaxed = true)
        
        every { collectionReference.document(pubId) } returns documentReference
        every { documentReference.get() } returns task
        coEvery { task.await() } returns snapshot
        every { snapshot.toObject(Publication::class.java) } returns publication

        // Act
        val result = repository.getPublicationById(pubId)

        // Assert
        assertTrue(result.isSuccess)
    }
}
