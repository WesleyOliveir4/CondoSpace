package com.example.condospace.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavBarRoutes {

    @Serializable
    object Home : NavBarRoutes()

    @Serializable
    object Favorites : NavBarRoutes()

    @Serializable
    object Publish : NavBarRoutes()

    @Serializable
    object Profile : NavBarRoutes()

}