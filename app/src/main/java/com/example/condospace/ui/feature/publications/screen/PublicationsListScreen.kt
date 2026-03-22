package com.example.condospace.ui.feature.publications.screen


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
import com.example.condospace.ui.component.TopBarReturn
import com.example.condospace.ui.feature.favorites.components.SearchPublications
import com.example.condospace.ui.mocks.PublicationsMocks
import com.example.condospace.ui.theme.CondoSpaceTheme

@Composable
fun PublicationsListScreen(
    navController: NavHostController,
    navigateToPublicationSelected: () -> Unit
) {
    CondoSpaceTheme {
        PublicationsListScreenContent(
            navController = navController,
            onPublicationClick = navigateToPublicationSelected
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsListScreenContent(
    navController: NavHostController,
    onPublicationClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Lista de publicações",
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
                publications = PublicationsMocks().getFavoritedPublications(),
                onPublicationClick = { onPublicationClick() }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        PublicationsListScreenContent(navController)
    }
}
