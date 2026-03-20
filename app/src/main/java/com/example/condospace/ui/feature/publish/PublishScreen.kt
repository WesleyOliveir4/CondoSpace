package com.example.condospace.ui.feature.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.ui.components.CondoSpaceTopBar
import com.example.condospace.ui.components.CreatePublicationScreen
import com.example.condospace.ui.components.PublicationCardList
import com.example.condospace.ui.components.navBar.NavBar
import com.example.condospace.ui.mocks.PublicationsMocks
import com.example.condospace.ui.theme.CondoSpaceTheme

@Composable
fun PublishScreen(navController: NavHostController) {
    CondoSpaceTheme {
        PublishScreenContent(navController)
    }
}


@Composable
fun PublishScreenContent(navController: NavHostController) {
    Scaffold(
        bottomBar = { NavBar(navController, "Publicar") },
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
                CreatePublicationScreen()

                PublicationCardList(
                    title = "Minhas publicações",
                    publications = PublicationsMocks().getFavoritedPublications(),
                    onEditClick = { /* editar */ },
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
        PublishScreenContent(navController)
    }
}