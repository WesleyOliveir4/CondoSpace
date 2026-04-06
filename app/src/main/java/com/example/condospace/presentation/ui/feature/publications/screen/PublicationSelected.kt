package com.example.condospace.presentation.ui.feature.publications.screen

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.publications.components.BottomContactBar
import com.example.condospace.presentation.ui.feature.publications.components.PublicationDetails
import com.example.condospace.presentation.ui.feature.publications.components.PublicationImage
import com.example.condospace.presentation.ui.feature.publications.state.PublicationSelectedUiState
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationSelectedViewModel
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublicationSelectedScreen(
    navController: NavHostController,
    publicationId: String
) {
    val viewModel: PublicationSelectedViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(publicationId) {
        viewModel.loadPublication(publicationId)
    }

    CondoSpaceTheme {
        PublicationSelectedScreenContent(
            navController = navController,
            uiState = uiState,
            onFavoriteClick = { viewModel.onFavoriteClick() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationSelectedScreenContent(
    navController: NavHostController,
    uiState: PublicationSelectedUiState,
    onFavoriteClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Publicação",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            uiState.publication?.let { publication ->
                BottomContactBar(
                    price = publication.price,
                    onClick = {
                        // Ação de contato
                    }
                )
            }
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
                    }
                }
                uiState.publication != null -> {
                    val publication = uiState.publication
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        val imageUrls = publication.imageUrlList?.map { it.url } ?: emptyList()
                        PublicationImage(imageUrls = imageUrls)

                        PublicationDetails(
                            publication = publication,
                            isFavorite = uiState.isFavorite,
                            onFavoriteClick = {
                                onFavoriteClick()
                            },
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationSelectedScreenPreview() {
    val navController = rememberNavController()
    val mockPublication = PublicationsMocks().getFavoritedPublications()[0]

    CondoSpaceTheme {
        PublicationSelectedScreenContent(
            navController = navController,
            uiState = PublicationSelectedUiState(
                publication = mockPublication,
                isFavorite = true
            )
        )
    }
}
