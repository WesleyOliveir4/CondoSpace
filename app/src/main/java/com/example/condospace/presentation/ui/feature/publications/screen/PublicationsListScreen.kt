package com.example.condospace.presentation.ui.feature.publications.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.component.error.EmptyState
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.favorites.components.PublicationItem
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
    viewModel: PublicationsListViewModel = koinViewModel(),
    publicationsIds: List<String>?
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(categoryType, publicationsIds) {
        viewModel.loadPublications(categoryType, publicationsIds ?: emptyList())
    }

    CondoSpaceTheme {
        when (val state = uiState) {
            is PublicationsListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PublicationsListUiState.Success -> {
                PublicationsListScreenContent(
                    navController = navController,
                    uiState = state,
                    categoryType = categoryType,
                    navigateToPublicationSelected = navigateToPublicationSelected
                )
            }
            is PublicationsListUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsListScreenContent(
    navController: NavHostController,
    uiState: PublicationsListUiState.Success,
    categoryType: String,
    navigateToPublicationSelected: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = if (categoryType == "Todos") stringResource(R.string.publications_list_all) else categoryType,
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
            SearchPublications(
                publications = uiState.publications,
                emptyState = {
                    EmptyState(
                        title = stringResource(R.string.publications_empty_state)
                    )
                },
                itemContent = { publication ->
                    PublicationItem(
                        publication = publication,
                        onClick = { navigateToPublicationSelected(publication.id) }
                    )
                }
            )
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
            uiState = PublicationsListUiState.Success(emptyList()),
            categoryType = "Todos",
            navigateToPublicationSelected = {},
        )
    }
}
