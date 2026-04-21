package com.example.condospace.data.repositoryImpl.externalAPIs

import com.example.condospace.data.model.*
import com.example.condospace.data.remote.OpenCageService
import com.example.condospace.data.remote.ViaCepService
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExternalServiceRepositoryImplTest {

    private lateinit var viaCepService: ViaCepService
    private lateinit var openCageService: OpenCageService
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: ExternalServiceRepositoryImpl

    @Before
    fun setUp() {
        viaCepService = mockk()
        openCageService = mockk()
        firestore = mockk()
        repository = ExternalServiceRepositoryImpl(viaCepService, openCageService, firestore)
        
        // Mocking Tasks.await() if needed, but since we use relaxed mocks or specific task mocks:
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `getNearbyServices should return list of publications within 3km`() = runTest {
        // Arrange
        val cep = "01001000"
        val viaCepResponse = ViaCepResponse(
            street = "Praça da Sé",
            city = "São Paulo",
            neighborhood = "Sé",
            error = false
        )
        coEvery { viaCepService.getAddressByCep(cep) } returns viaCepResponse

        val openCageResponse = OpenCageResponse(
            results = listOf(
                OpenCageResult(
                    components = OpenCageComponents(postcode = "01001000"),
                    confidence = 10,
                    geometry = GeometryDto(lat = -23.5505, lng = -46.6333) // Sé, SP
                )
            ),
            status = OpenCageStatus(code = 200, message = "OK")
        )
        coEvery { openCageService.getGeocoding(any()) } returns openCageResponse

        // Mock Firestore
        val collectionRef = mockk<CollectionReference>()
        val querySnapshot = mockk<QuerySnapshot>()
        val document1 = mockk<DocumentSnapshot>()
        val document2 = mockk<DocumentSnapshot>()

        every { firestore.collection("services") } returns collectionRef
        val getTask = Tasks.forResult(querySnapshot)
        every { collectionRef.get() } returns getTask
        
        // Doc 1: Close (Distance < 3km) -> -23.55, -46.63 is very close to -23.5505, -46.6333
        every { document1.id } returns "-23.5500,-46.6330"
        val pub1 = Publication(title = "Close Service", price = 50.0)
        every { document1.toObject(Publication::class.java) } returns pub1
        
        // Doc 2: Far (Distance > 3km) -> -23.40, -46.60
        every { document2.id } returns "-23.4000,-46.6000"
        val pub2 = Publication(title = "Far Service", price = 100.0)
        every { document2.toObject(Publication::class.java) } returns pub2

        every { querySnapshot.documents } returns listOf(document1, document2)

        // Act
        val result = repository.getNearbyServices(cep)

        // Assert
        assertTrue(result.isSuccess)
        val services = result.getOrNull()
        assertEquals(1, services?.size)
        assertEquals("Close Service", services?.first()?.title)
    }

    @Test
    fun `getNearbyServices should return failure when CEP is not found`() = runTest {
        // Arrange
        val cep = "00000000"
        coEvery { viaCepService.getAddressByCep(cep) } returns ViaCepResponse(error = true)

        // Act
        val result = repository.getNearbyServices(cep)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("CEP não encontrado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExternalServiceById should return publication when exists`() = runTest {
        // Arrange
        val id = "service123"
        val docRef = mockk<DocumentReference>()
        val docSnapshot = mockk<DocumentSnapshot>()
        val publication = Publication(id = id, title = "Test Service")

        every { firestore.collection("services").document(id) } returns docRef
        every { docRef.get() } returns Tasks.forResult(docSnapshot)
        every { docSnapshot.toObject(Publication::class.java) } returns publication
        every { docSnapshot.id } returns id

        // Act
        val result = repository.getExternalServiceById(id)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Test Service", result.getOrNull()?.title)
    }

    @Test
    fun `getExternalServicesByIds should return list of publications`() = runTest {
        // Arrange
        val ids = listOf("id1", "id2")
        val collectionRef = mockk<CollectionReference>()
        val querySnapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        // Mocking the chain: firestore.collection().whereIn().get()
        val queryMock = mockk<com.google.firebase.firestore.Query>()
        every { firestore.collection("services") } returns collectionRef
        every { collectionRef.whereIn(any<com.google.firebase.firestore.FieldPath>(), ids) } returns queryMock
        every { queryMock.get() } returns Tasks.forResult(querySnapshot)
        
        every { querySnapshot.documents } returns listOf(doc1, doc2)
        every { doc1.id } returns "id1"
        every { doc1.toObject(Publication::class.java) } returns Publication(id = "id1", title = "P1")
        every { doc2.id } returns "id2"
        every { doc2.toObject(Publication::class.java) } returns Publication(id = "id2", title = "P2")

        // Act
        val result = repository.getExternalServicesByIds(ids)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }
}
