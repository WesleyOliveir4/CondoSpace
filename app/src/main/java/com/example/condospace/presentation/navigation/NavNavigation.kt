package com.example.condospace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.condospace.presentation.ui.feature.condominium.screen.SelectCondominiumScreen
import com.example.condospace.presentation.ui.feature.favorites.screen.FavoritesScreen
import com.example.condospace.presentation.ui.feature.home.screen.HomeScreen
import com.example.condospace.presentation.ui.feature.login.screen.LoginScreen
import com.example.condospace.presentation.ui.feature.profile.screen.ChangePasswordScreen
import com.example.condospace.presentation.ui.feature.profile.screen.ProfileScreen
import com.example.condospace.presentation.ui.feature.profile.screen.SettingsScreen
import com.example.condospace.presentation.ui.feature.profile.screen.UserDataScreen
import com.example.condospace.presentation.ui.feature.publications.screen.EditPublicationScreen
import com.example.condospace.presentation.ui.feature.publications.screen.PublicationSelectedScreen
import com.example.condospace.presentation.ui.feature.publications.screen.PublicationsListScreen
import com.example.condospace.presentation.ui.feature.publish.screen.PublishScreen
import com.example.condospace.presentation.ui.feature.register.screen.RegisterScreen

@Composable
fun NavNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LoginScreen
    ){

        composable<NavRoutes.LoginScreen> {
            LoginScreen(
                navController,
                navigateToRegister = {
                    navController.navigate(NavRoutes.RegisterScreen)
                },
                navigateToHome = {
                    navController.navigate(NavRoutes.Home)
                }
            )
        }

        composable<NavRoutes.RegisterScreen> {
            RegisterScreen(
                navController,
                navigateToSelectCondominium = { userId: String ->
                    navController.navigate(NavRoutes.SelectCondominiumScreen(
                        userId = userId,
                        registerFlow = true
                    ))
                }
            )
        }

        composable<NavRoutes.Home> {
            HomeScreen(
                navController,
                navigateToPublishList = { categoryType, publicationIds ->
                    navController.navigate(NavRoutes.PublicationsList(categoryType = categoryType, publicationsIds = publicationIds))
                },
                navigateToPublicationSelected = { publicationId, categoryType ->
                    navController.navigate(NavRoutes.PublicationSelected(publicationId = publicationId, categoryType = categoryType))
                },
                navigateToSelectCondominium = { userId: String ->
                    navController.navigate(NavRoutes.SelectCondominiumScreen(
                        userId = userId,
                    ))
                }
            )
        }

        composable<NavRoutes.Favorites> {
            FavoritesScreen(
                navController,
                navigateToPublicationSelected = { publicationId, categoryType ->
                    navController.navigate(NavRoutes.PublicationSelected(publicationId, categoryType))
                },
                navigateToSelectCondominium = { userId: String ->
                    navController.navigate(NavRoutes.SelectCondominiumScreen(
                        userId = userId,
                    ))
                }
            )
        }

        composable<NavRoutes.Publish> {
            PublishScreen(
                navController,
                navigateToPublicationSelected = { publicationId, categoryType ->
                    navController.navigate(
                        NavRoutes.PublicationSelected(publicationId = publicationId, categoryType = categoryType)
                    )
                },
                navigateToEditPublication = { publicationId: String ->
                    navController.navigate(
                        NavRoutes.EditPublicationScreen(publicationId = publicationId)
                    )
                },
                navigateToSelectCondominium = { userId: String ->
                    navController.navigate(NavRoutes.SelectCondominiumScreen(
                        userId = userId,
                    ))
                }
            )
        }

        composable<NavRoutes.ChangePasswordScreen> {
            ChangePasswordScreen(navController)
        }

        composable<NavRoutes.SettingsScreen> {
            SettingsScreen(navController)
        }

        composable<NavRoutes.Profile> {
            ProfileScreen(
                navController,
                navigateToUserData = {
                    navController.navigate(NavRoutes.UserDataScreen)
                },
                navigateToSelectCondominium = { userId: String ->
                    navController.navigate(NavRoutes.SelectCondominiumScreen(
                        userId = userId,
                    ))
                },
                navigateToChangePassword = {
                    navController.navigate(NavRoutes.ChangePasswordScreen)
                },
                navigateToSettings = {
                    navController.navigate(NavRoutes.SettingsScreen)
                },
                navigateToLogin = {
                    navController.navigate(NavRoutes.LoginScreen)
                }
            )
        }

        composable<NavRoutes.PublicationsList> { backStackEntry ->

            val route = backStackEntry.toRoute<NavRoutes.PublicationsList>()

            PublicationsListScreen(
                navController,
                navigateToPublicationSelected = { publicationId ->
                    navController.navigate(NavRoutes.PublicationSelected(publicationId = publicationId))
                },
                categoryType = route.categoryType,
                publicationsIds = route.publicationsIds
            )
        }

        composable<NavRoutes.PublicationSelected> { backStackEntry ->

            val route = backStackEntry.toRoute<NavRoutes.PublicationSelected>()
            PublicationSelectedScreen(
                navController,
                publicationId = route.publicationId,
                categoryType = route.categoryType
            )
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

        composable<NavRoutes.SelectCondominiumScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoutes.SelectCondominiumScreen>()

            SelectCondominiumScreen(
                navController,
                userId = route.userId,
                registerFlow = route.registerFlow,
                navigateToLogin = {
                    navController.navigate(NavRoutes.LoginScreen)
                }
            )
        }

    }

}
