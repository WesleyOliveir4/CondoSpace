package com.example.condospace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.condospace.presentation.ui.feature.condominium.SelectCondominiumScreen
import com.example.condospace.presentation.ui.feature.favorites.screen.FavoritesScreen
import com.example.condospace.presentation.ui.feature.home.screen.HomeScreen
import com.example.condospace.presentation.ui.feature.profile.screen.ProfileScreen
import com.example.condospace.presentation.ui.feature.profile.screen.UserDataScreen
import com.example.condospace.presentation.ui.feature.publications.screen.EditPublicationScreen
import com.example.condospace.presentation.ui.feature.publications.screen.PublicationSelectedScreen
import com.example.condospace.presentation.ui.feature.publications.screen.PublicationsListScreen
import com.example.condospace.presentation.ui.feature.publish.screen.PublishScreen

@Composable
fun NavNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home
    ){

        composable<NavRoutes.Home> {
            HomeScreen(
                navController,
                navigateToPublishList = {
                    navController.navigate(NavRoutes.PublicationsList)
                },
                navigateToPublicationSelected = {
                    navController.navigate(NavRoutes.PublicationSelected)
                },
                navigateToSelectCondominium = {
                    navController.navigate(NavRoutes.SelectCondominiumScreen)
                }
            )
        }

        composable<NavRoutes.Favorites> {
            FavoritesScreen(
                navController,
                navigateToPublicationSelected = {
                    navController.navigate(NavRoutes.PublicationSelected)
                },
                navigateToSelectCondominium = {
                    navController.navigate(NavRoutes.SelectCondominiumScreen)
                }
            )
        }

        composable<NavRoutes.Publish> {
            PublishScreen(
                navController,
                navigateToPublicationSelected = {
                    navController.navigate(NavRoutes.PublicationSelected)
                },
                navigateToEditPublication = { publicationId: Int ->
                    navController.navigate(
                        NavRoutes.EditPublicationScreen(publicationId = publicationId)
                    )
                },
                navigateToSelectCondominium = {
                    navController.navigate(NavRoutes.SelectCondominiumScreen)
                }
            )
        }

        composable<NavRoutes.Profile> {
            ProfileScreen(
                navController,
                navigateToUserData = {
                    navController.navigate(NavRoutes.UserDataScreen)
                },
                navigateToSelectCondominium = {
                    navController.navigate(NavRoutes.SelectCondominiumScreen)
                }
            )
        }

        composable<NavRoutes.PublicationsList> {
            PublicationsListScreen(
                navController,
                navigateToPublicationSelected = {
                    navController.navigate(NavRoutes.PublicationSelected)
                }
            )
        }

        composable<NavRoutes.PublicationSelected> {
            PublicationSelectedScreen(navController)
        }

        composable<NavRoutes.EditPublicationScreen> { backStackEntry ->

            val route = backStackEntry.toRoute<NavRoutes.EditPublicationScreen>()

            EditPublicationScreen(
                navController,
                publicationId = route.publicationId
            )
        }

        composable<NavRoutes.UserDataScreen> {
            UserDataScreen(navController)
        }

        composable<NavRoutes.SelectCondominiumScreen> {
            SelectCondominiumScreen(navController)
        }

    }

}