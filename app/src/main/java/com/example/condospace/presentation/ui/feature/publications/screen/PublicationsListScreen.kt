package com.example.condospace.presentation.ui.feature.publications.screen


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
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.favorites.components.SearchPublications
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun PublicationsListScreen(
    navController: NavHostController,
    navigateToPublicationSelected: (String) -> Unit
) {
    CondoSpaceTheme {
        PublicationsListScreenContent(
            navController = navController,
            navigateToPublicationSelected = navigateToPublicationSelected
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationsListScreenContent(
    navController: NavHostController,
    navigateToPublicationSelected: (String) -> Unit
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
                onPublicationClick = { id ->
                    navigateToPublicationSelected(id)
                }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun  PublicationsListScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        PublicationsListScreenContent(
            navController,
            navigateToPublicationSelected = {},
        )
    }
}
