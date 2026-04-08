package com.example.condospace.presentation.ui.feature.publish.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.R
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.feature.publish.components.CreatePublicationScreen
import com.example.condospace.presentation.ui.feature.publish.components.PublicationCardList
import com.example.condospace.presentation.ui.feature.publish.state.PublishUiState
import com.example.condospace.presentation.ui.feature.publish.viewmodel.PublishViewModel
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublishScreen(
    navController: NavHostController,
    navigateToPublicationSelected: (String) -> Unit,
    navigateToEditPublication: (String) -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
) {
    val publishViewModel: PublishViewModel = koinViewModel()
    val uiState by publishViewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        when (val state = uiState) {
            is PublishUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PublishUiState.Success -> {
                PublishScreenContent(
                    navController = navController,
                    uiState = state,
                    publishViewModel = publishViewModel,
                    navigateToPublicationSelected = navigateToPublicationSelected,
                    navigateToEditPublication = navigateToEditPublication,
                    navigateToSelectCondominium = navigateToSelectCondominium
                )

                state.actionError?.let { message ->
                    ErrorDialog(
                        message = message,
                        onDismiss = { publishViewModel.resetActionState() }
                    )
                }

                LaunchedEffect(state.publishSuccess) {
                    if (state.publishSuccess) {
                        publishViewModel.resetActionState()
                    }
                }
            }

            is PublishUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { publishViewModel.retry() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreenContent(
    navController: NavHostController,
    uiState: PublishUiState.Success,
    publishViewModel: PublishViewModel,
    navigateToPublicationSelected: (String) -> Unit,
    navigateToEditPublication: (String) -> Unit,
    navigateToSelectCondominium: (String) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Publish") },
        topBar = {
            CondoSpaceTopBar(
                condominiumName = uiState.condominiumName,
                residenceSelector = {
                    navigateToSelectCondominium(uiState.user.uuid)
                }
            )
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                CreatePublicationScreen(
                    uiState.user,
                    onPublicationCreated = { publication ->
                        publishViewModel.createPublication(publication)
                    }
                )

                PublicationCardList(
                    title = stringResource(R.string.publication_my_publications),
                    publications = uiState.myPublications,
                    isLoading = uiState.isListLoading,
                    onItemClick = { publication ->
                        navigateToPublicationSelected(publication.id)
                    },
                    onEditClick = { publication ->
                        navigateToEditPublication(
                            publication.id
                        )
                    },
                    onDeleteClick = { publication ->
                        publishViewModel.deletePublication(publication.id)
                    }
                )

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublishScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        PublishScreenContent(
            navController = navController,
            uiState = PublishUiState.Success(
                user = UserUiModel(),
                condominiumName = "Condomínio Exemplo",
                myPublications = PublicationsMocks().getFavoritedPublications(),
                isListLoading = false,
                isPublishing = false,
                publishSuccess = false,
                actionError = null
            ),
            publishViewModel = koinViewModel(),
            navigateToPublicationSelected = {},
            navigateToEditPublication = {},
            navigateToSelectCondominium = {}
        )
    }
}
