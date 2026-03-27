package com.example.condospace.presentation.ui.feature.favorites.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.feature.favorites.components.SearchPublications
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    navigateToPublicationSelected: () -> Unit,
    navigateToSelectCondominium: () -> Unit
) {
    CondoSpaceTheme {
        FavoritesScreenContent(navController, navigateToPublicationSelected, navigateToSelectCondominium)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreenContent(
    navController: NavHostController,
    navigateToPublicationSelected: () -> Unit,
    navigateToSelectCondominium: () -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Favorites") },
        topBar = {
            CondoSpaceTopBar(
                residenceSelector = {
                    navigateToSelectCondominium()
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
                PublicationsMocks().getFavoritedPublications(),
                onPublicationClick = {
                    navigateToPublicationSelected()
                }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        FavoritesScreenContent(
            navController,
            navigateToPublicationSelected = {},
            navigateToSelectCondominium = {}
        )
    }
}