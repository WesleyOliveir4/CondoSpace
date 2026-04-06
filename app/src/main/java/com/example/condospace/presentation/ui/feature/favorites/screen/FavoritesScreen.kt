package com.example.condospace.presentation.ui.feature.favorites.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.feature.favorites.components.PublicationItem
import com.example.condospace.presentation.ui.feature.favorites.components.SearchPublications
import com.example.condospace.presentation.ui.feature.favorites.state.FavoritesUiState
import com.example.condospace.presentation.ui.feature.favorites.viewmodel.FavoritesViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    navigateToPublicationSelected: (String) -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        FavoritesScreenContent(
            navController = navController,
            uiState = uiState,
            navigateToPublicationSelected = navigateToPublicationSelected,
            navigateToSelectCondominium = navigateToSelectCondominium
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreenContent(
    navController: NavHostController,
    uiState: FavoritesUiState,
    navigateToPublicationSelected: (String) -> Unit,
    navigateToSelectCondominium: (String) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Favorites") },
        topBar = {
            CondoSpaceTopBar(
                condominiumName = uiState.condominiumName,
                residenceSelector = {
                    navigateToSelectCondominium(uiState.userUuid)
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
            SearchPublications(
                publications = uiState.publications
            ) { publication ->
                PublicationItem(
                    publication = publication,
                    onClick = { navigateToPublicationSelected(publication.id) }
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        FavoritesScreenContent(
            navController = navController,
            uiState = FavoritesUiState(
                condominiumName = "Condomínio Exemplo",
                userUuid = "123"
            ),
            navigateToPublicationSelected = {},
            navigateToSelectCondominium = {}
        )
    }
}
