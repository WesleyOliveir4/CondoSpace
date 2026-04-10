package com.example.condospace.presentation.ui.feature.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.R
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.navigation.NavRoutes
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.enums.CategoryType
import com.example.condospace.presentation.ui.feature.home.components.CategoriesSection
import com.example.condospace.presentation.ui.feature.home.components.EmptyPublicationsState
import com.example.condospace.presentation.ui.feature.home.components.PublicationsSection
import com.example.condospace.presentation.ui.feature.home.mocks.PublicationsMocksPreview
import com.example.condospace.presentation.ui.feature.home.state.HomeUiState
import com.example.condospace.presentation.ui.feature.home.viewmodel.HomeViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    navigateToPublishList: (String, List<String>?) -> Unit,
    navigateToPublicationSelected: (String, String?) -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF354EAB))
                }
            }

            is HomeUiState.Success -> {
                HomeScreenContent(
                    navController = navController,
                    uiState = state,
                    onRefresh = { viewModel.refreshPublications() },
                    navigateToPublishList = navigateToPublishList,
                    navigateToPublicationSelected = navigateToPublicationSelected,
                    navigateToSelectCondominium = navigateToSelectCondominium,
                    onCategoryClick = { categoryType ->
                        val filteredIds = viewModel.getPublicationIdsByCategory(categoryType.title)
                        navigateToPublishList(categoryType.title, filteredIds)
                    }
                )

                state.actionError?.let { message ->
                    ErrorDialog(
                        message = message,
                        onDismiss = { viewModel.resetActionError() }
                    )
                }
            }

            is HomeUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { viewModel.retry() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    navController: NavHostController,
    uiState: HomeUiState.Success,
    onRefresh: () -> Unit,
    navigateToPublishList: (String, List<String>?) -> Unit,
    navigateToPublicationSelected: (String, String?) -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
    onCategoryClick: (CategoryType) -> Unit
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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(innerPadding)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    CategoriesSection(
                        onCategoryClick = onCategoryClick
                    )


                    if (uiState.publicationsService.isEmpty() && uiState.publicationsRecommendation.isEmpty()) {
                        EmptyPublicationsState(
                            onAnnounceClick = {
                                navController.navigate(NavRoutes.Publish) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))

                        PublicationsSection(
                            title = stringResource(R.string.home_services_title),
                            publications = uiState.publicationsService,
                            onSeeMoreClick = { type, listIds ->
                                navigateToPublishList(type, listIds)
                                             },
                            onItemClick = { id, type ->
                                navigateToPublicationSelected(id, type)
                            }
                        )

                        PublicationsSection(
                            title = stringResource(R.string.home_recommendations_title),
                            publications = uiState.publicationsRecommendation,
                            onSeeMoreClick = { type, listIds ->
                                navigateToPublishList(type, listIds)
                            },
                            onItemClick = { id, type ->
                                navigateToPublicationSelected(id, type)
                            }
                        )

                        PublicationsSection(
                            title = "Serviços próximos de você",
                            publications = uiState.externalServices,
                            onSeeMoreClick = { type, listIds ->
                                navigateToPublishList(type, listIds)
                            },
                            onItemClick = { id, type ->
                                navigateToPublicationSelected(id, type)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Preview(name = "Standard Device", showBackground = true, showSystemUi = true)
@Preview(name = "Small Device", device = Devices.PIXEL_4, showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {

    val navController = rememberNavController()
    CondoSpaceTheme {
        HomeScreenContent(
            navController = navController,
            uiState = HomeUiState.Success(
                condominiumName = "Condomínio Exemplo",
                user = UserUiModel(
                    uuid = "123",
                    condominium = CondominiumUiModel(name = "Exemplo", cep = "00000-000")
                ),
                publicationsService = PublicationsMocksPreview().listMockUi,
                publicationsRecommendation = PublicationsMocksPreview().listMockUi
            ),
            onRefresh = {},
            navigateToPublishList = { _, _ -> },
            navigateToPublicationSelected = { _, _ -> },
            navigateToSelectCondominium = {},
            onCategoryClick = {}
        )
    }
}
