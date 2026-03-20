package com.example.condospace.ui.feature.favorites.screen

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
import com.example.condospace.ui.components.navBar.NavBar
import com.example.condospace.ui.components.CondoSpaceTopBar
import com.example.condospace.ui.feature.favorites.components.SearchPublications
import com.example.condospace.ui.mocks.PublicationsMocks
import com.example.condospace.ui.theme.CondoSpaceTheme

@Composable
fun FavoritesScreen(navController: NavHostController) {
    CondoSpaceTheme {
        FavoritesScreenContent(navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreenContent(navController: NavHostController) {
    Scaffold(
        bottomBar = { NavBar(navController, "Favorites") },
        topBar = {
            CondoSpaceTopBar()
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            SearchPublications(PublicationsMocks().getFavoritedPublications())
        }
    }

}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        FavoritesScreenContent(navController)
    }
}