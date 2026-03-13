package com.example.condospace.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.condospace.ui.feature.favorites.FavoritesScreen
import com.example.condospace.ui.feature.home.HomeScreen
import com.example.condospace.ui.feature.profile.ProfileScreen
import com.example.condospace.ui.feature.publish.PublishScreen

@Composable
fun NavBarNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavBarRoutes.Home
    ){

        composable<NavBarRoutes.Home> {
            HomeScreen(navController)
        }

        composable<NavBarRoutes.Favorites> {
            FavoritesScreen(navController)
        }

        composable<NavBarRoutes.Publish> {
            PublishScreen(navController)
        }

        composable<NavBarRoutes.Profile> {
            ProfileScreen(navController)
        }

    }

}