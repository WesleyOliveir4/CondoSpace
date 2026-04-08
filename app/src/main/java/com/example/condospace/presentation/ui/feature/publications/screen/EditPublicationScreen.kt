package com.example.condospace.presentation.ui.feature.publications.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.R
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.publications.state.EditPublicationUiState
import com.example.condospace.presentation.ui.feature.publications.viewmodel.EditPublicationViewModel
import com.example.condospace.presentation.ui.feature.publish.components.PublicationForm
import com.example.condospace.presentation.ui.feature.publish.components.PublicationType
import com.example.condospace.presentation.ui.feature.publish.state.PublishUiState
import com.example.condospace.presentation.ui.feature.publish.viewmodel.PublishViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun EditPublicationScreen(
    navController: NavHostController,
    publicationId: String
) {
    val viewModel: EditPublicationViewModel = koinViewModel()
    val publishViewModel: PublishViewModel = koinViewModel()
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val publishUiState by publishViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(publicationId) {
        viewModel.loadPublication(publicationId)
    }

    val currentPublishState = publishUiState
    LaunchedEffect(currentPublishState) {
        if (currentPublishState is PublishUiState.Success && currentPublishState.publishSuccess) {
            publishViewModel.resetActionState()
            navController.popBackStack()
        }
    }

    CondoSpaceTheme {
        when (val state = uiState) {
            is EditPublicationUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EditPublicationUiState.Success -> {
                val user = if (currentPublishState is PublishUiState.Success) currentPublishState.user else UserUiModel()
                
                EditPublicationScreenContent(
                    navController = navController,
                    publication = state.publication,
                    user = user,
                    onUpdatePublication = { updatedPublication ->
                        publishViewModel.createPublication(updatedPublication)
                    }
                )

                if (currentPublishState is PublishUiState.Success) {
                    currentPublishState.actionError?.let { message ->
                        ErrorDialog(
                            message = message,
                            onDismiss = { publishViewModel.resetActionState() }
                        )
                    }
                }
            }
            is EditPublicationUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { navController.popBackStack() }
                )
            }
        }
    }
}


@Composable
fun EditPublicationScreenContent(
    navController: NavHostController,
    publication: PublicationUiModel,
    user: UserUiModel,
    onUpdatePublication: (PublicationUiModel) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = stringResource(R.string.publication_edit_title),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        val currentPublicationType = remember(publication.publicationType) {
            PublicationType.entries.find { it.value == publication.publicationType }
                ?: PublicationType.PRODUCT
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            PublicationForm(
                title = if (currentPublicationType == PublicationType.SERVICE)
                    stringResource(R.string.publication_info_service)
                else
                    stringResource(R.string.publication_info_ad),
                publicationType = currentPublicationType,
                onPublish = { updatedPublication ->
                    onUpdatePublication(updatedPublication)
                },
                initialPublication = publication,
                user = user,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditPublicationScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        EditPublicationScreenContent(
            navController = navController,
            publication = PublicationUiModel(
                id = "1",
                publicationOwnerUuid = "1",
                publicationCondominiumId = "1",
                publicationOwner = "Owner",
                title = "Title",
                description = "Description",
                publicationType = "PRODUCT",
                price = 10.0,
                likes = 0,
                date = "2023-01-01"
            ),
            user = UserUiModel(),
            onUpdatePublication = {}
        )
    }
}
