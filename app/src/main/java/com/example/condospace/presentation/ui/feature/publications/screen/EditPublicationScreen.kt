package com.example.condospace.presentation.ui.feature.publications.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.publications.state.EditPublicationUiState
import com.example.condospace.presentation.ui.feature.publications.viewmodel.EditPublicationViewModel
import com.example.condospace.presentation.ui.feature.publish.components.PublicationForm
import com.example.condospace.presentation.ui.feature.publish.components.PublicationType
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

    LaunchedEffect(publishUiState.publishSuccess) {
        if (publishUiState.publishSuccess) {
            publishViewModel.resetActionState()
            navController.popBackStack()
        }
    }

    CondoSpaceTheme {
        EditPublicationScreenContent(
            navController = navController,
            uiState = uiState,
            user = publishUiState.user,
            onUpdatePublication = { updatedPublication ->
                publishViewModel.createPublication(updatedPublication)
            }
        )
    }
}


@Composable
fun EditPublicationScreenContent(
    navController: NavHostController,
    uiState: EditPublicationUiState,
    user: UserUiModel,
    onUpdatePublication: (PublicationUiModel) -> Unit
) {

    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Editar publicação",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                uiState.publication?.let { publication ->
                    val currentPublicationType = remember(publication.publicationType) {
                        PublicationType.entries.find { it.value == publication.publicationType }
                            ?: PublicationType.PRODUCT
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp)
                    ) {
                        PublicationForm(
                            title = if (currentPublicationType == PublicationType.SERVICE)
                                "Informações do serviço"
                            else
                                "Informações do anúncio",
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
            uiState = EditPublicationUiState(),
            user = UserUiModel(),
            onUpdatePublication = {}
        )
    }
}
