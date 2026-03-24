package com.example.condospace.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {

    @Serializable
    object Home : NavRoutes()

    @Serializable
    object Favorites : NavRoutes()

    @Serializable
    object Publish : NavRoutes()

    @Serializable
    object Profile : NavRoutes()
    @Serializable
    object PublicationsList : NavRoutes()
    @Serializable
    object PublicationSelected : NavRoutes()

}