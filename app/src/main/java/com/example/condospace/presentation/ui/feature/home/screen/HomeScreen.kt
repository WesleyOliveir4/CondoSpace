package com.example.condospace.presentation.ui.feature.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.enums.CategoryType
import com.example.condospace.presentation.ui.feature.home.components.CategoriesSection
import com.example.condospace.presentation.ui.feature.home.components.PublicationsSection
import com.example.condospace.presentation.ui.feature.home.state.HomeUiState
import com.example.condospace.presentation.ui.feature.home.viewmodel.HomeViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    navigateToPublishList: (String) -> Unit = {},
    navigateToPublicationSelected: (String) -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        HomeScreenContent(
            navController = navController,
            uiState = uiState,
            navigateToPublishList = navigateToPublishList,
            navigateToPublicationSelected = navigateToPublicationSelected,
            navigateToSelectCondominium = navigateToSelectCondominium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    navController: NavHostController,
    uiState: HomeUiState,
    navigateToPublishList: (String) -> Unit,
    navigateToPublicationSelected: (String) -> Unit,
    navigateToSelectCondominium: (String) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Home") },
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
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    CategoriesSection(
                        onCategoryClick = { categoryType ->
                            navigateToPublishList(categoryType.title)
                        }
                    )

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    PublicationsSection(
                        title = "Recomendados pelo seu condomínio",
                        publications = uiState.publications,
                        onSeeMoreClick = {
                            navigateToPublishList(CategoryType.SERVICES.title)
                        },
                        onItemClick = {id ->
                            navigateToPublicationSelected(id)
                        }
                    )

                    PublicationsSection(
                        title = "Serviços em destaque na região",
                        publications = uiState.publications.reversed(),
                        onSeeMoreClick = {
                            navigateToPublishList(CategoryType.SERVICES.title)
                        },
                        onItemClick = { id ->
                            navigateToPublicationSelected(id)
                        }
                    )
                }
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
            navController = navController,
            uiState = HomeUiState(
                condominiumName = "Condomínio Exemplo",
                user = UserUiModel(uuid = "123", condominium = CondominiumUiModel(name = "Exemplo", cep = "00000-000"))
            ),
            navigateToPublishList = {},
            navigateToPublicationSelected = {},
            navigateToSelectCondominium = {}
        )
    }
}
