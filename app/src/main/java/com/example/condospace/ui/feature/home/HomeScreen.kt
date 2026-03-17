package com.example.condospace.ui.feature.home

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
import com.example.condospace.ui.components.navBar.NavBar
import com.example.condospace.ui.components.CategoriesSection
import com.example.condospace.ui.components.CondoSpaceTopBar
import com.example.condospace.ui.components.PublicationsSection
import com.example.condospace.ui.mocks.PublicationsMocks
import com.example.condospace.ui.theme.CondoSpaceTheme

@Composable
fun HomeScreen(navController: NavHostController) {
    CondoSpaceTheme {
        HomeScreenContent(navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(navController: NavHostController) {
    Scaffold(
        bottomBar = { NavBar(navController, "Home") },
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
                CategoriesSection()


                PublicationsSection(
                    title = "Recomendados pelo seu condomínio",
                    publications = PublicationsMocks().getPublications()
                )


                PublicationsSection(
                    title = "Serviços em destaque na região",
                    publications = PublicationsMocks().getExternalPublications()
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        HomeScreenContent(navController)
    }
}
