package com.example.condospace.presentation.ui.feature.publish.viewmodel

import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.UserEntity
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.usecase.publication.CreatePublicationUseCase
import com.example.condospace.domain.usecase.publication.DeletePublicationUseCase
import com.example.condospace.domain.usecase.publication.GetPublicationsByUserUseCase
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.ui.feature.publish.state.PublishUiState
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
class PublishViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var getPublicationsByUserUseCase: GetPublicationsByUserUseCase
    private lateinit var createPublicationUseCase: CreatePublicationUseCase
    private lateinit var deletePublicationUseCase: DeletePublicationUseCase
    private lateinit var viewModel: PublishViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val userDataFlow = kotlinx.coroutines.flow.MutableStateFlow<UserEntity?>(null)

    private fun createPublicationEntity(id: String = "1") = PublicationEntity(
        id = id,
        publicationOwnerUuid = "uid",
        publicationCondominiumId = "condoId",
        publicationOwner = "Owner",
        title = "Title",
        description = "Desc",
        publicationType = "Type",
        price = 10.0,
        likes = 0,
        date = "2023-10-10"
    )

    private fun createPublicationUiModel(id: String = "1") = PublicationUiModel(
        id = id,
        publicationOwner = "Owner",
        title = "Title",
        description = "Desc",
        publicationType = "Type",
        price = 10.0,
        date = "2023-10-10"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk()
        getPublicationsByUserUseCase = mockk()
        createPublicationUseCase = mockk()
        deletePublicationUseCase = mockk()

        every { userPreferencesRepository.userData } returns userDataFlow
        every { getPublicationsByUserUseCase(any()) } returns flowOf(Result.success(emptyList()))

        viewModel = PublishViewModel(
            userPreferencesRepository,
            getPublicationsByUserUseCase,
            createPublicationUseCase,
            deletePublicationUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMyPublications should update myPublications list when successful`() = runTest {
        // Arrange
        val userId = "user123"
        val publications = listOf(createPublicationEntity(id = "1"))
        // GetPublicationsByUserUseCase returns a Flow
        every { getPublicationsByUserUseCase(userId) } returns flowOf(Result.success(publications))
        
        // We need to be in Success state for updateSuccessState to work
        val userEntity = UserEntity(uuid = userId, name = "Test", email = "test@test.com", phoneNumber = "", profilePicture = null, publicationsIdFavored = emptyList(), userIsLogged = true)
        every { userPreferencesRepository.userData } returns flowOf(userEntity)
        val viewModel = PublishViewModel(userPreferencesRepository, getPublicationsByUserUseCase, createPublicationUseCase, deletePublicationUseCase)

        // Act
        viewModel.loadMyPublications(userId)

        // Assert
        val state = viewModel.uiState.value as PublishUiState.Success
        assertEquals(1, state.myPublications.size)
        assertEquals("1", state.myPublications[0].id)
    }

    @Test
    fun `createPublication should update success state when successful`() = runTest {
        // Arrange
        val publicationUi = createPublicationUiModel()
        coEvery { createPublicationUseCase(any()) } returns Result.success(Unit)
        
        val userEntity = UserEntity(uuid = "uid", name = "Test", email = "test@test.com", phoneNumber = "", profilePicture = null, publicationsIdFavored = emptyList(), userIsLogged = true)
        
        // Act
        userDataFlow.value = userEntity
        viewModel.createPublication(publicationUi)

        // Assert
        val state = viewModel.uiState.value as PublishUiState.Success
        assertTrue(state.publishSuccess)
    }

    @Test
    fun `deletePublication should update success state when successful`() = runTest {
        // Arrange
        val publicationId = "1"
        coEvery { deletePublicationUseCase(publicationId) } returns Result.success(Unit)
        
        val userEntity = UserEntity(uuid = "uid", name = "Test", email = "test@test.com", phoneNumber = "", profilePicture = null, publicationsIdFavored = emptyList(), userIsLogged = true)
        
        // Act
        userDataFlow.value = userEntity
        viewModel.deletePublication(publicationId)

        // Assert
        val state = viewModel.uiState.value as PublishUiState.Success
        assertTrue(state.deleteSuccess)
    }
}
