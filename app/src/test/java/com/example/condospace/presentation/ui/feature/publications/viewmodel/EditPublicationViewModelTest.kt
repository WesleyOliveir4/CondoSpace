package com.example.condospace.presentation.ui.feature.publications.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.EditPublicationUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationByIdUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.ui.feature.publications.state.EditPublicationUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditPublicationViewModelTest {

    private lateinit var getPublicationByIdUseCase: GetPublicationByIdUseCase
    private lateinit var editPublicationUseCase: EditPublicationUseCase
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var viewModel: EditPublicationViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val userDataFlow = kotlinx.coroutines.flow.MutableStateFlow<UserEntity?>(null)

    private fun createPublicationEntity(id: String = "1", title: String = "Test") = PublicationEntity(
        id = id,
        publicationOwnerUuid = "uid",
        publicationCondominiumId = "condoId",
        publicationOwner = "Owner",
        title = title,
        description = "Desc",
        publicationType = "Type",
        price = 10.0,
        likes = 0,
        date = "2023-10-10"
    )

    private fun createPublicationUiModel(id: String = "1", title: String = "Test") = PublicationUiModel(
        id = id,
        publicationOwner = "Owner",
        title = title,
        description = "Desc",
        publicationType = "Type",
        price = 10.0,
        date = "2023-10-10"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPublicationByIdUseCase = mockk()
        editPublicationUseCase = mockk()
        userPreferencesRepository = mockk()

        every { userPreferencesRepository.userData } returns userDataFlow

        viewModel = EditPublicationViewModel(
            getPublicationByIdUseCase,
            editPublicationUseCase,
            userPreferencesRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPublication should update state to Success when successful`() = runTest {
        // Arrange
        val publicationId = "1"
        val publicationEntity = createPublicationEntity(id = publicationId)
        coEvery { getPublicationByIdUseCase(publicationId) } returns Result.success(publicationEntity)

        // Act
        viewModel.loadPublication(publicationId)

        // Assert
        assertTrue(viewModel.uiState.value is EditPublicationUiState.Success)
        assertEquals(publicationId, (viewModel.uiState.value as EditPublicationUiState.Success).publication.id)
    }

    @Test
    fun `loadPublication should update state to Error when fails`() = runTest {
        // Arrange
        val publicationId = "1"
        val errorMessage = "Error loading"
        coEvery { getPublicationByIdUseCase(publicationId) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.loadPublication(publicationId)

        // Assert
        assertTrue(viewModel.uiState.value is EditPublicationUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as EditPublicationUiState.Error).message)
    }

    @Test
    fun `updatePublication should update success state when successful`() = runTest {
        // Arrange
        val publicationUi = createPublicationUiModel(id = "1", title = "New Title")
        val initialPublication = createPublicationEntity(id = "1", title = "Old")
        coEvery { getPublicationByIdUseCase("1") } returns Result.success(initialPublication)
        viewModel.loadPublication("1")

        coEvery { editPublicationUseCase(any()) } returns Result.success(Unit)

        // Act
        viewModel.updatePublication(publicationUi)

        // Assert
        val state = viewModel.uiState.value as EditPublicationUiState.Success
        assertTrue(state.updateSuccess)
        assertEquals(false, state.isUpdating)
    }

    @Test
    fun `updatePublication should update error state when fails`() = runTest {
        // Arrange
        val publicationUi = createPublicationUiModel(id = "1", title = "New Title")
        val initialPublication = createPublicationEntity(id = "1", title = "Old")
        coEvery { getPublicationByIdUseCase("1") } returns Result.success(initialPublication)
        viewModel.loadPublication("1")

        val errorMessage = "Update failed"
        coEvery { editPublicationUseCase(any()) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.updatePublication(publicationUi)

        // Assert
        val state = viewModel.uiState.value as EditPublicationUiState.Success
        assertEquals(errorMessage, state.actionError)
        assertEquals(false, state.isUpdating)
    }

    @Test
    fun `observeUserData should update user in state when user data changes`() = runTest {
        // Arrange
        val userEntity = UserEntity(
            uuid = "uid123",
            name = "Test User",
            email = "test@test.com",
            phoneNumber = "",
            profilePicture = null,
            publicationsIdFavored = emptyList(),
            userIsLogged = true
        )
        
        val initialPublication = createPublicationEntity(id = "1", title = "Old")
        coEvery { getPublicationByIdUseCase("1") } returns Result.success(initialPublication)
        
        // Act
        userDataFlow.value = userEntity
        viewModel.loadPublication("1")

        // Assert
        val state = viewModel.uiState.value as EditPublicationUiState.Success
        assertEquals("Test User", state.user.name)
    }
}
