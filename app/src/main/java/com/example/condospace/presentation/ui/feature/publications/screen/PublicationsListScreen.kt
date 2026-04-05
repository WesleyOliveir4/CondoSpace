package com.example.condospace.presentation.ui.feature.publications.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.favorites.components.SearchPublications
import com.example.condospace.presentation.ui.feature.publications.state.PublicationsListUiState
import com.example.condospace.presentation.ui.feature.publications.viewmodel.PublicationsListViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublicationsListScreen(
    navController: NavHostController,
    categoryType: String,
    navigateToPublicationSelected: (String) -> Unit,
    viewModel: PublicationsListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(categoryType) {
        viewModel.init(categoryType)
    }

    CondoSpaceTheme {
        PublicationsListScreenContent(
            navController = navController,
            uiState = uiState,
            categoryType = categoryType,
            navigateToPublicationSelected = navigateToPublicationSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsListScreenContent(
    navController: NavHostController,
    uiState: PublicationsListUiState,
    categoryType: String,
    navigateToPublicationSelected: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = if (categoryType == "Todos") "Todas as publicações" else categoryType,
                onBackClick = { navController.popBackStack() }
            )
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
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    SearchPublications(
                        publications = uiState.publications,
                        onPublicationClick = { id ->
                            navigateToPublicationSelected(id)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationsListScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        PublicationsListScreenContent(
            navController = navController,
            uiState = PublicationsListUiState(),
            categoryType = "Todos",
            navigateToPublicationSelected = {},
        )
    }
}
