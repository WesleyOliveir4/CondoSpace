package com.example.condospace.presentation.ui.feature.home.screen

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
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.feature.home.components.CategoriesSection
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.feature.home.components.PublicationsSection
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    navigateToPublishList: () -> Unit = {},
    navigateToPublicationSelected: () -> Unit
) {
    CondoSpaceTheme {
        HomeScreenContent(
            navController,
            navigateToPublishList,
            navigateToPublicationSelected
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    navController: NavHostController,
    navigateToPublishList: () -> Unit,
    navigateToPublicationSelected: () -> Unit
)
{
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
                CategoriesSection(
                    onCategoryClick = {
                        navigateToPublishList()
                    }
                )


                PublicationsSection(
                    title = "Recomendados pelo seu condomínio",
                    publications = PublicationsMocks().getPublications(),
                    onSeeMoreClick = {
                        navigateToPublishList()
                    },
                    onItemClick = {
                        navigateToPublicationSelected()
                    }
                )


                PublicationsSection(
                    title = "Serviços em destaque na região",
                    publications = PublicationsMocks().getExternalPublications(),
                    onSeeMoreClick = {
                        navigateToPublishList()
                    },
                    onItemClick = {
                        navigateToPublicationSelected()
                    }
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
        HomeScreenContent(
            navController,
            navigateToPublishList = {},
            navigateToPublicationSelected = {}
        )
    }
}
