package com.example.condospace.presentation.ui.feature.favorites.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.component.error.EmptyState
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.feature.favorites.components.PublicationItem
import com.example.condospace.presentation.ui.feature.favorites.components.SearchPublications
import com.example.condospace.presentation.ui.feature.favorites.state.FavoritesUiState
import com.example.condospace.presentation.ui.feature.favorites.viewmodel.FavoritesViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    navigateToPublicationSelected: (String, String?) -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FavoritesUiState.Success -> {
                FavoritesScreenContent(
                    navController = navController,
                    uiState = state,
                    navigateToPublicationSelected = navigateToPublicationSelected,
                    navigateToSelectCondominium = navigateToSelectCondominium
                )
            }
            is FavoritesUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreenContent(
    navController: NavHostController,
    uiState: FavoritesUiState.Success,
    navigateToPublicationSelected: (String, String?) -> Unit,
    navigateToSelectCondominium: (String) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Favorites") },
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
            SearchPublications(
                publications = uiState.publications,
                emptyState = {
                    EmptyState(
                        title = "Nenhuma publicação\n favoritada."
                    )
                }
            ) { publication ->
                PublicationItem(
                    publication = publication,
                    onClick = { navigateToPublicationSelected(publication.id,publication.publicationType) }
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
            uiState = FavoritesUiState.Success(
                user = UserUiModel(uuid = "123", name = "Teste"),
                condominiumName = "Condomínio Exemplo"
            ),
            navigateToPublicationSelected = { _, _ ->
            },
            navigateToSelectCondominium = {}
        )
    }
}
