package com.example.condospace.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    object LoginScreen : NavRoutes()

    @Serializable
    object RegisterScreen : NavRoutes()


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
    
    @Serializable
    data class EditPublicationScreen(val publicationId: Int) : NavRoutes()
    
    @Serializable
    object UserDataScreen : NavRoutes()
    @Serializable
        object SelectCondominiumScreen : NavRoutes()

}