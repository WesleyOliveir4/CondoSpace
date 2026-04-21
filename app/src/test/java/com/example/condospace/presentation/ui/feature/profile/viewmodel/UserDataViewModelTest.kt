package com.example.condospace.presentation.ui.feature.profile.viewmodel

import android.net.Uri
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.user.GetUserUseCase
import com.example.condospace.domain.usecase.user.UpdateUserUseCase
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.ui.feature.profile.state.UserDataUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDataViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var updateUserUseCase: UpdateUserUseCase
    private lateinit var imageRepository: ImageRepository
    private lateinit var publicationRepository: PublicationRepository
    private lateinit var viewModel: UserDataViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow<UserEntity?>(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        getUserUseCase = mockk()
        updateUserUseCase = mockk()
        imageRepository = mockk()
        publicationRepository = mockk()

        coEvery { userPreferencesRepository.userData } returns userDataFlow
        
        // Stub padrão para evitar "no answer found"
        coEvery { publicationRepository.getPublicationsByUser(any()) } returns flowOf(Result.success(emptyList()))
        coEvery { publicationRepository.updatePublication(any()) } returns Result.success(Unit)

        mockkStatic("com.example.condospace.presentation.model.UserUiModelKt")
        mockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")

        viewModel = UserDataViewModel(
            userPreferencesRepository,
            getUserUseCase,
            updateUserUseCase,
            imageRepository,
            publicationRepository
        )
    }

    @After
    fun tearDown() {
        unmockkStatic("com.example.condospace.presentation.model.UserUiModelKt")
        unmockkStatic("com.example.condospace.presentation.model.PublicationUiModelKt")
        Dispatchers.resetMain()
    }

    private fun createTestPublication(
        id: String = "p1",
        owner: String = "Old",
        type: String = "Venda",
        contact: String = "123"
    ) = PublicationEntity(
        id = id,
        publicationOwnerUuid = "u1",
        publicationCondominiumId = "c1",
        publicationOwner = owner,
        contact = contact,
        title = "Title",
        description = "Desc",
        publicationType = type,
        price = 10.0,
        likes = 0,
        date = "2023-01-01"
    )

    @Test
    fun `viewModel should fetch data from Firebase when local user is emitted`() = runTest(testDispatcher) {
        val userLocal = UserEntity(uuid = "user123", name = "Local", phoneNumber = "", profilePicture = null, email = "")
        val userRemote = userLocal.copy(name = "Remote")
        
        coEvery { getUserUseCase("user123") } returns Result.success(userRemote)

        userDataFlow.value = userLocal
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UserDataUiState.Success)
        assertEquals("Remote", (viewModel.uiState.value as UserDataUiState.Success).user.name)
    }

    @Test
    fun `updateUserName should update user and its publications`() = runTest(testDispatcher) {
        val userEntity = UserEntity(uuid = "u1", name = "Old", phoneNumber = "123", profilePicture = null, email = "")
        coEvery { getUserUseCase("u1") } returns Result.success(userEntity)
        userDataFlow.value = userEntity
        advanceUntilIdle()

        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)
        
        val pub = createTestPublication(owner = "Old")
        coEvery { publicationRepository.getPublicationsByUser("u1") } returns flowOf(Result.success(listOf(pub)))

        viewModel.updateUserName("New Name")
        advanceUntilIdle()

        coVerify { updateUserUseCase(match { it.name == "New Name" }) }
        coVerify { publicationRepository.updatePublication(match { it.publicationOwner == "New Name" }) }
        
        val state = viewModel.uiState.value as UserDataUiState.Success
        assertEquals("New Name", state.user.name)
    }

    @Test
    fun `updateProfilePicture should handle upload failure`() = runTest(testDispatcher) {
        val user = UserEntity(uuid = "u1", name = "T", phoneNumber = "", profilePicture = null, email = "")
        coEvery { getUserUseCase("u1") } returns Result.success(user)
        userDataFlow.value = user
        advanceUntilIdle()

        val uri = mockk<Uri>()
        coEvery { imageRepository.uploadImages(any(), any()) } returns Result.failure(Exception("Upload Failed"))

        viewModel.updateProfilePicture(uri)
        advanceUntilIdle()

        val state = viewModel.uiState.value as UserDataUiState.Success
        assertEquals("Upload Failed", state.error)
        assertFalse(state.isUpdating)
    }

    @Test
    fun `updateUserPhone should update contact in publications except recommendations`() = runTest(testDispatcher) {
        val userEntity = UserEntity(uuid = "u1", name = "N", phoneNumber = "old", profilePicture = null, email = "")
        coEvery { getUserUseCase("u1") } returns Result.success(userEntity)
        userDataFlow.value = userEntity
        advanceUntilIdle()

        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)
        
        val servicePub = createTestPublication(id = "s1", type = ServiceType.SERVICE.value, contact = "old")
        val recoPub = createTestPublication(id = "r1", type = ServiceType.RECOMMENDATION.value, contact = "reco-contact")
        
        coEvery { publicationRepository.getPublicationsByUser("u1") } returns flowOf(Result.success(listOf(servicePub, recoPub)))

        viewModel.updateUserPhone("new-phone")
        advanceUntilIdle()

        coVerify { publicationRepository.updatePublication(match { it.contact == "new-phone" }) }
        coVerify(exactly = 0) { publicationRepository.updatePublication(match { it.publicationType == ServiceType.RECOMMENDATION.value && it.contact == "new-phone" }) }
    }

    @Test
    fun `clearMessages should nullify error and successMessage`() = runTest(testDispatcher) {
        val user = UserEntity(uuid = "u1", name = "T", phoneNumber = "", profilePicture = null, email = "")
        coEvery { getUserUseCase("u1") } returns Result.success(user)
        userDataFlow.value = user
        advanceUntilIdle()

        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)
        viewModel.updateUserName("New")
        advanceUntilIdle()
        
        assertTrue((viewModel.uiState.value as UserDataUiState.Success).successMessage != null)

        viewModel.clearMessages()
        runCurrent()

        val state = viewModel.uiState.value as UserDataUiState.Success
        assertNull(state.error)
        assertNull(state.successMessage)
    }

    @Test
    fun `fetchUserFirebaseData should show error when user is not found in firebase`() = runTest(testDispatcher) {
        val userLocal = UserEntity(uuid = "u1", name = "L", phoneNumber = "", profilePicture = null, email = "")
        coEvery { getUserUseCase("u1") } returns Result.success(null)

        userDataFlow.value = userLocal
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UserDataUiState.Error)
        assertEquals("Usuário não encontrado", (viewModel.uiState.value as UserDataUiState.Error).message)
    }

    @Test
    fun `updateProfilePicture should show updating state`() = runTest(testDispatcher) {
        val user = UserEntity(uuid = "u1", name = "T", phoneNumber = "", profilePicture = null, email = "")
        coEvery { getUserUseCase("u1") } returns Result.success(user)
        userDataFlow.value = user
        advanceUntilIdle()

        val uri = mockk<Uri>()
        val deferred = CompletableDeferred<Result<List<PublicationImageEntity>>>()
        coEvery { imageRepository.uploadImages(any(), any()) } coAnswers { deferred.await() }

        viewModel.updateProfilePicture(uri)
        runCurrent()

        assertTrue((viewModel.uiState.value as UserDataUiState.Success).isUpdating)

        deferred.complete(Result.success(listOf(PublicationImageEntity("url", "id"))))
        coEvery { updateUserUseCase(any()) } returns Result.success(Unit)
        advanceUntilIdle()

        assertFalse((viewModel.uiState.value as UserDataUiState.Success).isUpdating)
    }
}
