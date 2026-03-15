package com.example.condospace.ui.feature.favorites

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.navigation.NavBar
import com.example.condospace.ui.components.ResidenceSelector
import com.example.condospace.ui.components.SearchPublications
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
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),

                title = {
                    ResidenceSelector(
                        text = "Residencial Green Park",
                        onClick = {
                            // abrir tela para selecionar seu condomínio
                        }
                    )
                },

                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notificações"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFFF6F6F6)
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