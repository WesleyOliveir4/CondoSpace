package com.example.condospace.presentation.ui.feature.publish.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.feature.publish.components.CreatePublicationScreen
import com.example.condospace.presentation.ui.feature.publish.components.PublicationCardList
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun PublishScreen(
    navController: NavHostController,
    navigateToPublicationSelected: () -> Unit,
    navigateToEditPublication: (Int) -> Unit
) {
    CondoSpaceTheme {
        PublishScreenContent(
            navController,
            navigateToPublicationSelected,
            navigateToEditPublication
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreenContent(
    navController: NavHostController,
    navigateToPublicationSelected: () -> Unit,
    navigateToEditPublication: (Int) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Publish") },
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
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                CreatePublicationScreen(
                    onPublicationCreated = { publication ->
                        Log.e("Publicacao Criada", "$publication")
                    }
                )

                PublicationCardList(
                    title = "Minhas publicações",
                    publications = PublicationsMocks().getFavoritedPublications(),
                    onEditClick = { publication ->
                        navigateToEditPublication(
                            publication.id
                        )
                    },
                    onDeleteClick = { /* deletar */ }
                )

            }

        }
    }

}



@Preview(showBackground = true)
@Composable
fun PublishScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        PublishScreenContent(
            navController,
            navigateToPublicationSelected = {},
            navigateToEditPublication = {}
        )
    }
}